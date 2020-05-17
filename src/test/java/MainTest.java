import org.apache.commons.codec.Charsets;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.BufferedReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

public class MainTest {
    private Package test1 = new Package((byte) 1,2,3,4,"test message");
    private byte[] test1arr;
    private  Package test2;
    private  byte[] test2arr;


    @Before
    public void init() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        test1arr = fillArr((byte)1, 2, 3, 4, "test message");

        Random r = new Random(System.currentTimeMillis());
        byte a = (byte) r.nextInt(255);
        long b = r.nextLong();
        int c = r.nextInt();
        int d = r.nextInt();
        test2 = new Package(a,b,c,d,"rand");
        test2arr = fillArr(a,b,c,d,"rand");
    }

    public  byte[] fillArr(byte bSrc, long bPktId, int cType, int bUserId, String msg) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        byte[] message = msg.getBytes(Charsets.UTF_8);
        byte[] bMsq = ByteBuffer.allocate(message.length+8).order(ByteOrder.BIG_ENDIAN)
                .putInt(cType).putInt(bUserId).put(message).array();


        bMsq = Main.encrypt(bMsq);

        short crc2 = CRC16.getCrc(bMsq);


        byte[] pktIdLong = ByteBuffer.allocate(8).putLong(bPktId).array();
        byte[] part1 = {
                0x13,
                bSrc,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, (byte) bMsq.length
        };
        System.arraycopy(pktIdLong,0, part1, 2, 8);

        short crc1 = CRC16.getCrc(part1);

        byte[] res = ByteBuffer.allocate(part1.length+4+bMsq.length).order(ByteOrder.BIG_ENDIAN)
                .put(part1).putShort(crc1).put(bMsq).putShort(crc2).array();

        return res;
    }

    @Test
    public void decode1() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
       assertEquals(test1, Main.decode(test1arr));
    }
    @Test
    public void decode2() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        assertEquals(test2, Main.decode(test2arr));
    }
    @Test
    public void encode1() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        assertArrayEquals(Main.encode(test1), test1arr);
    }
    @Test
    public void encode2() throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        assertArrayEquals(Main.encode(test2), test2arr);
    }
}