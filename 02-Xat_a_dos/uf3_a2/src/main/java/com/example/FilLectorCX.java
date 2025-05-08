package com.example;
import java.io.*;

class FilLectorCX extends Thread {
    private ObjectInputStream in;
    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }
    @Override
    public void run() {
        try {
            String msg;
            while ((msg = (String) in.readObject()) != null) {
                System.out.println("Rebut: " + msg);
                if (msg.equals(ClientXat.MSG_SORTIR)) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // End of stream or error
        }
    }
}
