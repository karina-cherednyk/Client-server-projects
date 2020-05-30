package clases;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CipherProcessor {
    private Cipher cipher;
    private Key key;
//TODO обмен ключами
    public CipherProcessor() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.cipher  = Cipher.getInstance("Blowfish");
        this.key = new SecretKeySpec("TaIsIia`s_SECkey".getBytes(),"Blowfish");
        //this.key  = KeyGenerator.getInstance("Blowfish").generateKey();
    }

    public synchronized byte[] encrypt(byte[] input) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE,key);
        return cipher.doFinal(input);
    }
    public synchronized  byte[] decrypt(byte[] input) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE,key);
        return cipher.doFinal(input);
    }
}
