package com.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName(HOST));
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("Servidor aturat.");
    }

    public String getNom(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeObject("Escriu el teu nom:");
        out.flush();
        String nom = (String) in.readObject();
        return nom;
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            String nom = servidor.getNom(in, out);
            System.out.println("Nom rebut: " + nom);
            System.out.println("Fil de xat creat.");

            FilServidorXat fil = new FilServidorXat(in);
            System.out.println("Fil de " + nom + " iniciat");
            fil.start();

            Scanner sc = new Scanner(System.in);
            String msg;
            while (true) {
                System.out.print("Missatge ('" + MSG_SORTIR + "' per tancar): ");
                msg = sc.nextLine();
                out.writeObject(msg);
                out.flush();
                if (msg.equals(MSG_SORTIR)) {
                    break;
                }
            }
            sc.close();

            fil.join();
            System.out.println(msg);
            servidor.pararServidor();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}