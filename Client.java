import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.math.BigInteger;

class Client {
    public static void main (String args[]) throws Exception {
        Scanner kb = new Scanner(System.in);
        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        RSA rsa = new RSA();
        
        BigInteger[] plaintextClient;
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
                int bufferSize = (int) file.length();
                char[] plain = new char[bufferSize];
                reader.read(plain);
                reader.close();

                plaintextClient = new BigInteger[plain.length];
                for (int i = 0; i < plain.length; i++) {
                    plaintextClient[i] = BigInteger.valueOf((int) plain[i]);
                }
                break;
            }
            catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        outToServer.writeBytes("Success" + "\n");
        String publicKeyServer = inFromServer.readLine();
        String values[] = publicKeyServer.split(" ");
        BigInteger[] publicKey = {new BigInteger(values[0]), new BigInteger(values[1])};
        BigInteger[] ciphertextArray = rsa.encrypt(plaintextClient, publicKey);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciphertextArray.length; i++) {
            sb.append(ciphertextArray[i].toString());
            sb.append(" ");
        }
        String ciphertext = sb.toString().trim();
        String publicKeyString = rsa.getPublicKey();
        outToServer.writeBytes(ciphertext + "\n");
        outToServer.writeBytes(publicKeyString + "\n");

        String ciphertextServer = inFromServer.readLine();
        String[] asciiEncrypted = ciphertextServer.split(" ");
        BigInteger[] cipher = new BigInteger[asciiEncrypted.length];
        for (int i = 0; i < asciiEncrypted.length; i++) {
            cipher[i] = new BigInteger(asciiEncrypted[i]);
        }
        BigInteger[] plaintextArray = rsa.decrypt(cipher);
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < plaintextArray.length; i++) {
            sb2.append((char) plaintextArray[i].intValue());
        }
        String plaintextServer = sb2.toString().trim();
        System.out.println("Received message: " + plaintextServer);

        kb.close();
        clientSocket.close();
    }
}