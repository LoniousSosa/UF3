package com.example;

import java.io.*;

class FilServidorXat extends Thread {
    private ObjectInputStream in;
    public FilServidorXat(ObjectInputStream in) {
        this.in = in;
    }
    @Override
    public void run() {
        try {
            String msg;
            while ((msg = (String) in.readObject()) != null) {
                if (msg.equals(ServidorXat.MSG_SORTIR)) {
                    break;
                }
                System.out.println("Rebut: " + msg);
            }
            System.out.println("Fil de xat finalitzat.");
        } catch (IOException | ClassNotFoundException e) {
            // End of stream or error
        }
    }
}