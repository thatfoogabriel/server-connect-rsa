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
        ByteArrayOutputStream bytesToServer = new ByteArrayOutputStream();
        
        byte[] plaintext;
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
                FileInputStream reader = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                reader.close();
                plaintext = out.toByteArray();
                break;
            } 
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        byte[] ciphertext = rsa.encrypt(plaintext);
        byte[] publicKey = rsa.getPublicKey().getEncoded();
        bytesToServer.write(ciphertext);
        for (int i=0; i< ciphertext.length; i++)
            System.out.print(ciphertext[i]);
        bytesToServer.write(publicKey);
        System.out.println();
        for (int i=0; i< publicKey.length; i++)
            System.out.print(publicKey[i]);
        outToServer.write(bytesToServer.toByteArray());

        /*byte[] ciphertextClient = new byte[0];
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
        System.out.println(new String(decryptedtext));*/
        
        kb.close();
        clientSocket.close();
    }
}