import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

class Server {
    public static void main (String args[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            String greeting = "Listening for messages...";
            System.out.println(greeting);
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            DataInputStream bytesFromClient = new DataInputStream(connectionSocket.getInputStream());
            RSA rsa = new RSA();
            
            byte[] ciphertextClient = new byte[1024];
            byte[] publicKeyBytes = new byte[1024];
            String message = inFromClient.readLine();
            if (message.equals("Quit")) {
                System.out.println("Shutting down...");
                connectionSocket.close();
                break;
            }
            try {
                int ciphertextSize = bytesFromClient.read(ciphertextClient);
                int publicKeySize = bytesFromClient.read(publicKeyBytes);
            }
            catch (IOException e) {
                System.out.println("Error reading message: " + e.getMessage());
            }
            for (int i=0; i< ciphertextClient.length; i++)
                System.out.print(ciphertextClient[i]);
            System.out.println();
            for (int i=0; i< publicKeyBytes.length; i++)
                System.out.print(publicKeyBytes[i]);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKeyClient = keyFactory.generatePublic(publicKeySpec);

            byte[] decryptedtext = rsa.decrypt(ciphertextClient, publicKeyClient);
            System.out.println("Received message: " + new String(decryptedtext));

            /*String plain = "";
            try {
                File file = new File("SeverResponse.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while (reader.readLine() != null) {
                    plain += reader.readLine() + "\n";
                }
                reader.close();
                break;
            } 
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }

            byte[] plaintext = plain.getBytes();
            byte[] ciphertext = rsa.encrypt(plaintext);
            byte[] publicKey = rsa.getPublicKey().getEncoded();
            outToClient.write(ciphertext);
            outToClient.write('\n');
            outToClient.write(publicKey);*/

            connectionSocket.close();
        }
        welcomeSocket.close();
    }
}