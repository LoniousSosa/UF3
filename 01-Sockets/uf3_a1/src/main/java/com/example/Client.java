package com.example;
// Client.java
import java.io.*;
import java.net.*;

public class Client {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void connecta() throws IOException {
        // Obre socket al servidor
        socket = new Socket(HOST, PORT);
        // Crea un PrintWriter per enviar línies
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
    }

    public void envia(String msg) {
        out.println(msg);
        System.out.println("Enviat al servidor: " + msg);
    }

    public void tanca() throws IOException {
        if (out != null)
            out.close();
        if (socket != null && !socket.isClosed())
            socket.close();
        System.out.println("Client tancat");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connecta();
            client.envia("Prova d’enviament 1");
            client.envia("Prova d’enviament 2");
            client.envia("Adeu!");
            System.out.println("Prem Enter per tancar el client...");
            System.in.read(); // Espera pulsació d'ENTER
            client.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}