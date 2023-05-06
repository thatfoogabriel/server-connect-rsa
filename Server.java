import java.io.*;
import java.net.*;

class Server {
    public static void main (String args[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            String name = "Listening for messages...";
            System.out.println(name);
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String message = inFromClient.readLine();
            if (message.equals("Quit")) {
                System.out.println("Shutting down...");
                connectionSocket.close();
                break;
            }

            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            connectionSocket.close();
        }
        welcomeSocket.close();
    }
}