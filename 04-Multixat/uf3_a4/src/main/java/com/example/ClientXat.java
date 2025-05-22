// ClientXat.java
package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat: " + socket.getRemoteSocketAddress());
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatgeRaw) throws IOException {
        System.out.println("Enviant missatge: " + missatgeRaw);
        if (out != null) {
            out.writeObject(missatgeRaw);
            out.flush();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("---------------------");
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            String cru;
            while (!sortir && (cru = (String) in.readObject()) != null) {
                if (cru.trim().isEmpty()) continue;
                String codi = Missatge.getCodiMissatge(cru);
                String[] parts = Missatge.getPartsMissatge(cru);
                if (codi == null || parts == null) continue;

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        System.out.println("Missatge personal per (" + parts[1] + ") de (" + parts[0] + "): " + parts[2]);
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        System.out.println("[Grup] " + parts[1]);
                        break;
                    default:
                        System.err.println("Codi invàlid des del servidor: " + cru);
                }
            }
        } catch (ClassNotFoundException e) {
            // ignorar
        } catch (IOException e) {
            System.err.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc) -> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String msg, boolean obligatori) {
        String lin;
        do {
            System.out.print(msg + (obligatori ? " (obligatori): " : ": "));
            lin = sc.nextLine().trim();
        } while (obligatori && lin.isEmpty());
        return lin;
    }

    public void tancarClient() {
        try { if (in  != null) in.close();  } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket!= null) socket.close(); } catch (IOException ignored) {}
        System.out.println("Flux d'entrada tancat.");
        System.out.println("Flux de sortida tancat.");
        System.out.println("Tancant client...");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try (Scanner scanner = new Scanner(System.in)) {
            client.connecta();

            boolean llegitConnectar = false;
            boolean sortirLoop = false;

            while (!sortirLoop) {
                client.ajuda();
                String op = client.getLinea(scanner, "Opció", false);
                if (op.isEmpty()) {
                    sortirLoop = true;
                } else {
                    switch (op) {
                        case "1":
                            String nom = client.getLinea(scanner, "Introdueix el nom", true);
                            client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                            client.start();  // ara sí comença a llegir
                            llegitConnectar = true;
                            break;
                        case "2":
                            if (!llegitConnectar) {
                                System.out.println("Has de connectar abans!");
                                break;
                            }
                            String dest = client.getLinea(scanner, "Destinatari", true);
                            String msgP = client.getLinea(scanner, "Missatge personal", true);
                            client.enviarMissatge(Missatge.getMissatgePersonal(dest, msgP));
                            break;
                        case "3":
                            if (!llegitConnectar) {
                                System.out.println("Has de connectar abans!");
                                break;
                            }
                            String msgG = client.getLinea(scanner, "Missatge de grup", true);
                            client.enviarMissatge(Missatge.getMissatgeGrup(msgG));
                            break;
                        case "4":
                            client.enviarMissatge(Missatge.getMissatgeSortirClient(""));
                            sortirLoop = true;
                            break;
                        case "5":
                            client.enviarMissatge(Missatge.getMissatgeSortirTots(""));
                            sortirLoop = true;
                            break;
                        default:
                            System.out.println("Opció no vàlida.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de comunicació: " + e.getMessage());
        } finally {
            client.tancarClient();
        }
        System.exit(0);
    }
}