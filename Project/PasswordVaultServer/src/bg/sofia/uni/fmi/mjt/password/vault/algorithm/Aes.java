package bg.sofia.uni.fmi.mjt.password.vault.algorithm;

import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidDecryptionException;
import bg.sofia.uni.fmi.mjt.password.vault.exception.InvalidEncryptionException;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Aes {

    private static final int FROM = 0;
    private static final int TO = 16;
    private static final String ALGO = "AES";

    public static String encrypt(String data, String keyValue) throws InvalidEncryptionException {
        try {
            String subKey = keyValue.substring(FROM, TO);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(subKey.getBytes(), ALGO));
            byte[] encVal = c.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            throw new InvalidEncryptionException("A problem occurred when try to encrypt message!", e);
        }
    }

    public static String decrypt(String encryptedData, String keyValue) throws InvalidDecryptionException {
        try {
            String subKey = keyValue.substring(FROM, TO);
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(subKey.getBytes(), ALGO));
            byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decordedValue);
            return new String(decValue);
        } catch (Exception e) {
            throw new InvalidDecryptionException("A problem occurred when try to decrypt message!", e);
        }
    }


    public static void main(String[] args) throws Exception {

        String key = Sha256.getSha256("1");
        String clean = "7t*679^[PfMPi8P99N5-9bbuPz";

        String encrypted = encrypt(clean, key);
        String decrypted = decrypt(encrypted, key);

        System.out.println(encrypted);
        System.out.println(decrypted);
    }
}