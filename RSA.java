import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    private BigInteger[] publicKey = {BigInteger.valueOf(0), BigInteger.valueOf(0)};
    private BigInteger[] privateKey = {BigInteger.valueOf(0), BigInteger.valueOf(0)};
    BigInteger p, q, n, z, e, d;

    public RSA() {
        SecureRandom random = new SecureRandom();
        p = BigInteger.probablePrime(8, random);
        q = BigInteger.probablePrime(8, random);
        n = p.multiply(q);
        z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.valueOf(2);
        while (z.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.ONE);
        }
        d = e.modInverse(z);
        publicKey[0] = n;
        publicKey[1] = e;
        privateKey[0] = n;
        privateKey[1] = d;
    }

    public BigInteger[] decrypt(BigInteger[] ciphertext) {
        BigInteger[] plaintext = new BigInteger[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i++) {
            plaintext[i] = ciphertext[i].modPow(d, n);
        }
        return plaintext;    
    }

    public BigInteger[] encrypt(BigInteger[] plaintext, BigInteger[] publicKey) {
        BigInteger n = publicKey[0];
        BigInteger e = publicKey[1];
        BigInteger[] ciphertext = new BigInteger[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = plaintext[i].modPow(e, n);
        }
        return ciphertext;
    }

    public String getPublicKey() {
        String keyString = "";
        for (int i=0; i<2; i++) {
            keyString += publicKey[i].toString() + " ";
        }
        return keyString;
    }
}