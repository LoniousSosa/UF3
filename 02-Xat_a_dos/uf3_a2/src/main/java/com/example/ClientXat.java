package com.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat a " + HOST + ":" + PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String msg) throws IOException {
        out.writeObject(msg);
        out.flush();
        System.out.println("Enviant missatge: " + msg);
    }

    public void tancarClient() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("Client tancat.");
        System.out.println("El servidor ha tancat la connexió.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();

            FilLectorCX fil = new FilLectorCX(client.in);
            System.out.println("Fil de lectura iniciat");
            fil.start();

            Scanner sc = new Scanner(System.in);
            String msg;
            // Primer, llegir nom
            msg = sc.nextLine();
            client.enviarMissatge(msg);

            while (true) {
                System.out.print("Missatge ('" + MSG_SORTIR + "' per tancar): ");
                msg = sc.nextLine();
                client.enviarMissatge(msg);
                if (msg.equals(MSG_SORTIR)) {
                    break;
                }
            }
            sc.close();

            fil.join();
            System.out.println("Tancant client...");
            client.tancarClient();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
