import org.apache.commons.codec.Charsets;

import javax.crypto.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Arrays;

public class Main {
    private static  Cipher cipher;
    private static Key key;
    static
    {
        try {
            cipher = Cipher.getInstance("Blowfish");
            key = KeyGenerator.getInstance("Blowfish").generateKey();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public  static  byte[] encrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE,key);
        return cipher.doFinal(input);
    }
    public  static  byte[] decrypt(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE,key);
        return cipher.doFinal(input);
    }
    public static byte[] encode(byte bSrc, long bPktId, int cType, int bUserId, String message) throws  InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] bMessage = message.getBytes(Charsets.UTF_8);
        byte[] bMsq = ByteBuffer.allocate(8+bMessage.length)
                                .order(ByteOrder.BIG_ENDIAN)
                                .putInt(cType).putInt(bUserId).put(bMessage).array();

        //encrypt message
        bMsq = Main.encrypt(bMsq);

        byte bMagic = 0x13;

        byte[] part1 =
                ByteBuffer.allocate(14)
                        .order(ByteOrder.BIG_ENDIAN)
                        .put(bMagic).put(bSrc).putLong(bPktId).putInt(bMsq.length).array();

        short wCrc1 = CRC16.getCrc(part1,0,part1.length);
        short wCrc2 = CRC16.getCrc(bMsq,0, bMsq.length);

        return ByteBuffer.allocate(part1.length+4+bMsq.length)
                        .order(ByteOrder.BIG_ENDIAN)
                        .put(part1).putShort(wCrc1).put(bMsq).putShort(wCrc2).array();

    }

    public static byte[] encode(Package p) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return encode(p.getbSrc(),p.getbPktId(),p.getcType(),p.getbUserId(),p.getMessage());
    }


    //if package is incorrect throw IllegalArgumentException
    public static Package decode(byte[] input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte bMagic = input[0];
        if(bMagic!= 0x13)
            throw new IllegalArgumentException("Magic byte expected");

        byte bSrc = input[1];
        long bPktId = ByteBuffer.wrap(input,2,8).order(ByteOrder.BIG_ENDIAN).getLong();
        int wLen = ByteBuffer.wrap(input,10,4).order(ByteOrder.BIG_ENDIAN).getInt();


        //-----check 1 ------//
        short expectedCrc1 = ByteBuffer.wrap(input,14,2).order(ByteOrder.BIG_ENDIAN).getShort();
        short actualCrc1 = CRC16.getCrc(input,0,14);

        if(expectedCrc1 != actualCrc1)
            throw new IllegalArgumentException("First crc check was not passed\n" +
                    "Expected crc: "+expectedCrc1+", actual crc:"+actualCrc1
            );


        //-----check 2 ------//
        short expectedCrc2 = ByteBuffer.wrap(input,16+wLen,2).order(ByteOrder.BIG_ENDIAN).getShort();
        short actualCrc2 =  CRC16.getCrc(input,16,wLen);

        if(expectedCrc2 != actualCrc2)
            throw new IllegalArgumentException("Second crc check was not passed\n" +
                    "Expected crc: "+expectedCrc2+", actual crc:"+actualCrc2
            );

        //decrypt message
        byte[] bMsq = new byte[wLen];
        System.arraycopy(input, 16, bMsq,0,wLen);
        bMsq = decrypt(bMsq);

        //parse message
        int cType = ByteBuffer.wrap(bMsq,0,4).order(ByteOrder.BIG_ENDIAN).getInt();
        int bUserId = ByteBuffer.wrap(bMsq,4,4).order(ByteOrder.BIG_ENDIAN).getInt();
        byte[] message = new byte[bMsq.length-8];
        System.arraycopy(bMsq, 8, message, 0,bMsq.length-8);


        return new Package(bSrc,bPktId,cType,bUserId, new String(message));
    }
}
