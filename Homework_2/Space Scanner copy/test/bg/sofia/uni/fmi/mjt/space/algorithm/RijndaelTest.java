package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RijndaelTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";

    private static SecretKey secretKey;

    @BeforeAll
    static void generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        secretKey = keyGenerator.generateKey();
    }

    @Test
    public void testEncryptThrowTheRightException() {
        SymmetricBlockCipher hasNullSecretKey = new Rijndael(null);

        assertThrows(CipherException.class,
                () -> hasNullSecretKey.encrypt(new ByteArrayInputStream("".getBytes()), new ByteArrayOutputStream()),
                "If there is a problem with encrypt, CipherException should be thrown");
    }

    @Test
    public void testEncryptAndDecrypt() throws CipherException {
        SymmetricBlockCipher cipher = new Rijndael(secretKey);
        String input = " This is a test input 123";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream encryptOutput = new ByteArrayOutputStream();
        OutputStream decryptedOutput = new ByteArrayOutputStream();

        cipher.encrypt(inputStream, encryptOutput);
        cipher.decrypt(new ByteArrayInputStream(encryptOutput.toByteArray()), decryptedOutput);

        assertEquals(input, decryptedOutput.toString(),
                "After encryption and decryption the output should be the same as the starting one");
    }


}
