package com.example;

// Servidor.java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Socket connectar() throws IOException {
        System.out.println("--- Servidor ---");
        System.out.printf("Acceptant connexions en -> %s:%d%n", HOST, PORT);
        serverSocket = new ServerSocket(PORT);
        System.out.println("Esperant connexió...");
        clientSocket = serverSocket.accept();
        System.out.println("Connexió acceptada: " + clientSocket.getRemoteSocketAddress());
        // Primer crear ObjectOutputStream per evitar bloqueig de capçaleres
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in  = new ObjectInputStream(clientSocket.getInputStream());
        return clientSocket;
    }

    public void enviarFitxers() {
        try {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) in.readObject();
            if (nomFitxer == null || nomFitxer.trim().isEmpty()) {
                System.err.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }
            System.out.println("Nom fitxer rebut: " + nomFitxer);
            Fitxer f = new Fitxer(nomFitxer);
            byte[] dades = f.getContingut();
            System.out.printf("Contingut del fitxer a enviar: %d bytes%n", dades.length);
            out.writeObject(dades);
            out.flush();
            System.out.println("Fitxer enviat al client: " + nomFitxer);
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer del client: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error en la recepció del nom del fitxer: " + e.getMessage());
        }
    }

    public void tancarConnexio() {
        try {
            if (clientSocket != null) {
                System.out.println("Tancant connexió amb el client: " + clientSocket.getRemoteSocketAddress());
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        try {
            s.connectar();
            s.enviarFitxers();
        } catch (IOException e) {
            System.err.println("Error de connexió: " + e.getMessage());
        } finally {
            s.tancarConnexio();
        }
    }
}