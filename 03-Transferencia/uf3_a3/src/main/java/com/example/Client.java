package com.example;

// Client.java
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String DIR_ARRIBADA = "/tmp"; // o "C:\\temp" en Windows

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner = new Scanner(System.in);

    public void connectar() throws IOException {
        System.out.println("\n--- Client ---");
        System.out.printf("Connectant a -> %s:%d%n", HOST, PORT);
        socket = new Socket(HOST, PORT);
        System.out.println("Connexió acceptada: " + socket.getRemoteSocketAddress());
        // Primer crear ObjectOutputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
    }

    public void rebreFitxers() {
        try {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String peticio = scanner.nextLine().trim();
                if ("sortir".equalsIgnoreCase(peticio)) {
                    System.out.println("Sortint...");
                    break;
                }
                System.out.print("Nom del fitxer a guardar: ");
                String desti = scanner.nextLine().trim();
                if (desti.isEmpty()) {
                    System.err.println("Ruta de destinació invàlida, s'ignora aquesta petició.");
                    continue;
                }

                // Enviem la petició al servidor
                out.writeObject(peticio);
                out.flush();

                // Rebem les dades
                Object resposta = in.readObject();
                if (resposta instanceof byte[]) {
                    byte[] dades = (byte[]) resposta;
                    try (FileOutputStream fos = new FileOutputStream(desti)) {
                        fos.write(dades);
                        System.out.println("Fitxer rebut i guardat com: " + desti);
                    }
                } else {
                    System.err.println("Resposta inesperada del servidor.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la transferència: " + e.getMessage());
        }
    }

    public void tancarConnexio() {
        try {
            if (socket != null) {
                socket.close();
            }
            System.out.println("Connexió tancada.");
        } catch (IOException e) {
            System.err.println("Error tancant connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
        try {
            c.connectar();
            c.rebreFitxers();
        } catch (IOException e) {
            System.err.println("Error de connexió: " + e.getMessage());
        } finally {
            c.tancarConnexio();
        }
    }
}