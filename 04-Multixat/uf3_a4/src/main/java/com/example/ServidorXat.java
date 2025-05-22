// ServidorXat.java
package com.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private final Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        while (!servidor.sortir) {
            try {
                Socket s = servidor.serverSocket.accept();
                System.out.println("Client connectat: " + s.getRemoteSocketAddress());
                GestorClients g = new GestorClients(s, servidor);
                g.start();
            } catch (IOException e) {
                System.err.println("Error acceptant connexió: " + e.getMessage());
            }
        }
        servidor.pararServidor();
    }

    public void servidorAEscoltar() {
        try {
            InetAddress addr = InetAddress.getByName(HOST);
            serverSocket = new ServerSocket(PORT, 50, addr);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("No s'ha pogut iniciar el servidor: " + e.getMessage());
            System.exit(1);
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error aturant el servidor: " + e.getMessage());
        }
    }

    public void finalizarXat() {
        clients.clear();
        System.exit(0);
    }

    public synchronized void afegirClient(GestorClients g) {
        String nom = g.getNom();
        clients.put(nom, g);
        enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " connectat."));
    }

    public synchronized void eliminarClient(String nom) {
        if (nom!=null && clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " desconnectat."));
        }
    }

    public synchronized void enviarMissatgeGrup(String raw) {
        for (GestorClients g: clients.values()) g.enviarMissatge(raw);
    }

    public synchronized void enviarMissatgePersonal(String dest, String rem, String msg) {
        GestorClients g = clients.get(dest);
        if (g!=null) g.enviarMissatge(Missatge.getMissatgePersonal(rem, msg));
    }
}