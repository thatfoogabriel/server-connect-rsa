import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

class Server {
    public static void main (String args[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            String greeting = "Listening for messages...";
            System.out.println(greeting);
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataInputStream dIn = new DataInputStream(connectionSocket.getInputStream());
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            RSA rsa = new RSA();
            
            byte[] ciphertextClient = new byte[dIn.readInt()];
            dIn.readFully(ciphertextClient);
            byte[] publicKeyBytes = new byte[dIn.readInt()];
            dIn.readFully(publicKeyBytes);
            String message = inFromClient.readLine();
            if (message.equals("Quit")) {
                System.out.println("Shutting down...");
                connectionSocket.close();
                break;
            }
            try {
                ByteArrayOutputStream ciphertextStream = new ByteArrayOutputStream();
                ByteArrayOutputStream publicKeyStream = new ByteArrayOutputStream();
                int byteRead;
                while ((byteRead = dIn.read()) != -1) {
                    ciphertextStream.write(byteRead);
                    if (dIn.available() == 0) {
                        break;
                    }
                }
                ciphertextClient = ciphertextStream.toByteArray();
                
                while ((byteRead = dIn.read()) != -1) {
                    publicKeyStream.write(byteRead);
                    if (dIn.available() == 0) {
                        break;
                    }
                }
                publicKeyBytes = publicKeyStream.toByteArray();
            }
            catch (IOException e) {
                System.out.println("Error reading message: " + e.getMessage());
            }

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKeyClient = keyFactory.generatePublic(publicKeySpec);

            byte[] decryptedtext = rsa.decrypt(ciphertextClient, publicKeyClient);
            System.out.println("Received message: " + new String(decryptedtext));

            String plain = "";
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
            outToClient.write(publicKey);

            connectionSocket.close();
        }
        welcomeSocket.close();
    }
}