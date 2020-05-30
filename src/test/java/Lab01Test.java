import clases.PackageProcessor;
import clases.CRC16;
import com.google.common.primitives.UnsignedLong;
import entities.Package;
import org.apache.commons.codec.Charsets;
import org.junit.Before;
import org.junit.Test;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Lab01Test {
    private Package test1 = new Package((byte) 1,UnsignedLong.asUnsigned(2),3,4,"test message");
    private byte[] test1arr;
    private  Package test2;
    private  byte[] test2arr;


    @Before
    public void init() throws Exception {
        test1arr = fillArr((byte)1, UnsignedLong.asUnsigned(2), 3, 4, "test message");

        Random r = new Random(System.currentTimeMillis());
        byte a = (byte) r.nextInt(255);
        UnsignedLong b = UnsignedLong.asUnsigned(r.nextLong());
        int c = r.nextInt();
        int d = r.nextInt();
        test2 = new Package(a,b,c,d,"rand");
        test2arr = fillArr(a,b,c,d,"rand");
    }

    private byte[] fillArr(byte bSrc, UnsignedLong bPktId, int cType, int bUserId, String msg) throws Exception {
        byte[] message = msg.getBytes(Charsets.UTF_8);
        byte[] bMsq = ByteBuffer.allocate(message.length+8).order(ByteOrder.BIG_ENDIAN)
                .putInt(cType).putInt(bUserId).put(message).array();


        bMsq = PackageProcessor.processor.encrypt(bMsq);

        short crc2 = CRC16.getCrc(bMsq);


        byte[] pktIdLong = ByteBuffer.allocate(8).putLong(bPktId.longValue()).array();
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
    static  int c;
    @Test
    public void decode1() throws Exception {
       assertEquals(test1, PackageProcessor.decode(test1arr));
    }
    @Test
    public void decode2() throws Exception {
        assertEquals(test2, PackageProcessor.decode(test2arr));
    }
    @Test
    public void encode1() throws Exception {
        assertArrayEquals(PackageProcessor.encode(test1), test1arr);
    }
    @Test
    public void encode2() throws Exception {
        assertArrayEquals(PackageProcessor.encode(test2), test2arr);
    }
    @Test
    public void counter() throws Exception {
        for(int i=0; i<30; ++i){
            Package p  = PackageProcessor.decode(fillArr((byte)i,UnsignedLong.asUnsigned(i),i,i,"test"));
            System.out.println(p);
            PackageProcessor.encode(p);
            System.out.println("success");
        }
    }
}