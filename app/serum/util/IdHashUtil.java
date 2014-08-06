package serum.util;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.xml.bind.DatatypeConverter;

/**
 * Thanks to http://www.code2learn.com/2011/06/encryption-and-decryption-of-data-using.html
 */
public class IdHashUtil
{
    private static final String ALGO = "AES";
    private static final byte[] keyBytes = new String("R8TrSr_hagLrA0ty").getBytes();

    public static String encrypt(Long id)
    throws Exception
    {
        Key key = new SecretKeySpec(keyBytes, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] decryptedBytes = Long.toString(id).getBytes();
        byte[] encryptedBytes = c.doFinal(decryptedBytes);
        return DatatypeConverter.printBase64Binary(encryptedBytes);
    }

    public static Long decrypt(String idHash)
    throws Exception
    {
        Key key = new SecretKeySpec(keyBytes, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] encryptedBytes = DatatypeConverter.parseBase64Binary(idHash);
        byte[] decryptedBytes = c.doFinal(encryptedBytes);
        return Long.parseLong(new String(decryptedBytes));
    }
}
