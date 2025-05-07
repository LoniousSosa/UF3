import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        // Crea el ServerSocket escoltant en HOST i PORT
        srvSocket = new ServerSocket(PORT, 0, InetAddress.getByName(HOST));
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        // Espera i accepta connexions de clients
        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());
    }

    public void repDades() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String msg;
        // Llegeix línies fins que el client tanqui o enviï "Adeu!"
        while ((msg = in.readLine()) != null) {
            System.out.println("Rebut: " + msg);
            if (msg.equals("Adeu!")) {
                break;
            }
        }
        in.close();
    }

    public void tanca() throws IOException {
        if (clientSocket != null && !clientSocket.isClosed())
            clientSocket.close();
        if (srvSocket != null && !srvSocket.isClosed())
            srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            servidor.connecta();
            servidor.repDades();
            servidor.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}