import java.io.*;
import java.math.BigInteger;
import java.net.*;

class Server {
    public static void main (String args[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        while (true) {
            String greeting = "Listening for messages...";
            System.out.println(greeting);
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            RSA rsa = new RSA();

            String message = inFromClient.readLine();
            if (message.equals("Quit")) {
                System.out.println("Shutting down...");
                connectionSocket.close();
                break;
            }

            String publicKeyString = rsa.getPublicKey();
            outToClient.writeBytes(publicKeyString + "\n");

            String ciphertextClient = inFromClient.readLine();
            String[] asciiEncrypted = ciphertextClient.split(" ");
            BigInteger[] ciphertext = new BigInteger[asciiEncrypted.length];
            for (int i = 0; i < asciiEncrypted.length; i++) {
                ciphertext[i] = new BigInteger(asciiEncrypted[i]);
            }
            BigInteger[] plaintextArray = rsa.decrypt(ciphertext);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < plaintextArray.length; i++) {
                sb.append((char) plaintextArray[i].intValue());
            }
            String plaintextClient = sb.toString().trim();
            System.out.println("Received message: " + plaintextClient + "\n");

            BigInteger[] plaintextServer = null;
            try {
                File file = new File("ServerResponse.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                int bufferSize = (int) file.length();
                char[] plain = new char[bufferSize];
                reader.read(plain);
                reader.close();

                plaintextServer = new BigInteger[plain.length];
                for (int i = 0; i < plain.length; i++) {
                    plaintextServer[i] = BigInteger.valueOf((int) plain[i]);
                }
            }
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }

            String publicKeyClient = inFromClient.readLine();
            String values[] = publicKeyClient.split(" ");
            BigInteger[] publicKey = {new BigInteger(values[0]), new BigInteger(values[1])};
            BigInteger[] ciphertextArray = rsa.encrypt(plaintextServer, publicKey);
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < ciphertextArray.length; i++) {
                sb2.append(ciphertextArray[i].toString());
                sb2.append(" ");
            }
            String cipher = sb2.toString().trim();
            String publicKeyStringServer = rsa.getPublicKey();
            outToClient.writeBytes(cipher + "\n");
            outToClient.writeBytes(publicKeyStringServer + "\n");

            connectionSocket.close();
        }
        welcomeSocket.close();
    }
}