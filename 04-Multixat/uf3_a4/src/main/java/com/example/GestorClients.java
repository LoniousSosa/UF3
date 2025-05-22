// GestorClients.java
package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private final Socket client;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) throws IOException {
        this.client = client;
        this.servidor = servidor;
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.in  = new ObjectInputStream(client.getInputStream());
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            // Primer missatge cru amb codi de connexió
            String cru = (String) in.readObject();
            processaMissatge(cru);

            while (!sortir) {
                cru = (String) in.readObject();
                processaMissatge(cru);
            }
        } catch (ClassNotFoundException e) {
            // ignorar
        } catch (IOException e) {
            System.err.println("Error amb el client " + nom + ": " + e.getMessage());
        } finally {
            try { out.close(); } catch (IOException ignored) {}
            try { in.close();  } catch (IOException ignored) {}
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    public void enviarMissatge(String missatgeRaw) {
        try {
            out.writeObject(missatgeRaw);
            out.flush();
        } catch (IOException e) {
            System.err.println("No s'ha pogut enviar missatge a " + nom + ": " + e.getMessage());
        }
    }

    private void processaMissatge(String cru) {
        String codi = Missatge.getCodiMissatge(cru);
        String[] parts = Missatge.getPartsMissatge(cru);
        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                System.out.println("DEBUG: multicast Entra: " + nom);
                break;

            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(nom);
                break;

            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                System.out.println("Tancant tots els clients.");
                System.out.println("DEBUG: multicast sortir");
                servidor.finalizarXat();
                break;

            case Missatge.CODI_MSG_PERSONAL:
                // 0=codi,1=dest,2=missatge
                servidor.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;

            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(cru);
                break;

            default:
                System.err.println("Codi no vàlid rebut: " + cru);
        }
    }
}