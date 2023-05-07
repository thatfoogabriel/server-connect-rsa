import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

class Client {
    public static void main (String args[]) throws Exception {
        Scanner kb = new Scanner(System.in);
        RSA rsa = new RSA();
        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
        
        String plain = "";
        while (true) {
            System.out.println("Enter File Name: ");
            String fileName = kb.nextLine();
            if (fileName.equals("Quit") || fileName.equals("quit")) {
                outToServer.writeBytes("Quit" + '\n');
                kb.close();
                clientSocket.close();
                System.exit(0);
            }
            try {
                File file = new File(fileName);
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
        }

        byte[] plaintext = plain.getBytes();
        byte[] ciphertext = rsa.encrypt(plaintext);
        byte[] publicKey = rsa.getPublicKey().getEncoded();
        dOut.write(ciphertext);
        dOut.write(publicKey);

        byte[] ciphertextClient = new byte[0];
        byte[] publicKeyBytes = new byte[0];
        try {
            ciphertextClient = inFromServer.readLine().getBytes();
            publicKeyBytes = inFromServer.readLine().getBytes();
        }
        catch (IOException e) {
            System.out.println("Error reading message: " + e.getMessage());
        }
        System.out.println("Success");
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKeyClient = keyFactory.generatePublic(publicKeySpec);

        byte[] decryptedtext = rsa.decrypt(ciphertextClient, publicKeyClient);
        System.out.println(new String(decryptedtext));
        
        kb.close();
        clientSocket.close();
    }
}