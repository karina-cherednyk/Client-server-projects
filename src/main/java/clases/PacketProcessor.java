package clases;

import com.google.common.primitives.UnsignedLong;
import entities.Packet;
import exceptions.MagicByteException;
import exceptions.WrongCrcException;
import org.apache.commons.codec.Charsets;

import javax.crypto.NoSuchPaddingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;


public class PacketProcessor {
    public  static CipherProcessor processor;

    static {
        try {
            processor = new CipherProcessor();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //lab 01

    public static byte[] encode(byte bSrc, UnsignedLong bPktId, int cType, int bUserId, String message) throws  Exception {
        byte[] bMessage = message.getBytes(Charsets.UTF_8);
        byte[] bMsq = ByteBuffer.allocate(8+bMessage.length)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(cType).putInt(bUserId).put(bMessage).array();

        //encrypt message
        bMsq = processor.encrypt(bMsq);

        byte bMagic = 0x13;

        byte[] part1 =
                ByteBuffer.allocate(14)
                        .order(ByteOrder.BIG_ENDIAN)
                        .put(bMagic).put(bSrc).putLong(bPktId.longValue()).putInt(bMsq.length).array();

        short wCrc1 = CRC16.getCrc(part1,0,part1.length);
        short wCrc2 = CRC16.getCrc(bMsq,0, bMsq.length);

        return ByteBuffer.allocate(part1.length+4+bMsq.length)
                .order(ByteOrder.BIG_ENDIAN)
                .put(part1).putShort(wCrc1).put(bMsq).putShort(wCrc2).array();

    }

    public static byte[] encode(Packet p) throws Exception {
        return encode(p.getbSrc(),p.getbPktId(),p.getcType(),p.getbUserId(),p.getMessage());
    }


    //if package is incorrect throw IllegalArgumentException
    public static Packet decode(byte[] input) throws Exception {

        ByteBuffer wrapper = ByteBuffer.wrap(input).order(ByteOrder.BIG_ENDIAN);
        byte bMagic = wrapper.get();
        if(bMagic!= 0x13)
            throw new MagicByteException(bMagic);

        byte bSrc = wrapper.get();
        UnsignedLong bPktId = UnsignedLong.asUnsigned(wrapper.getLong());
        int wLen = wrapper.getInt();


        //-----check 1 ------//
        short expectedCrc1 = wrapper.getShort();
        short actualCrc1 = CRC16.getCrc(input,0,14);

        if(expectedCrc1 != actualCrc1)
            throw new WrongCrcException("First crc check was not passed\n" +
                    "Expected crc: "+expectedCrc1+", actual crc:"+actualCrc1
            );


        //-----check 2 ------//
        short expectedCrc2 = ByteBuffer.wrap(input,16+wLen,2).order(ByteOrder.BIG_ENDIAN).getShort();
        short actualCrc2 =  CRC16.getCrc(input,16,wLen);

        if(expectedCrc2 != actualCrc2)
            throw new WrongCrcException("Second crc check was not passed\n" +
                    "Expected crc: "+expectedCrc2+", actual crc:"+actualCrc2
            );

        //decrypt message
        byte[] bMsq = new byte[wLen];
        System.arraycopy(input, 16, bMsq,0,wLen);
        bMsq = processor.decrypt(bMsq);

        //parse message
        ByteBuffer msqWrapper = ByteBuffer.wrap(bMsq).order(ByteOrder.BIG_ENDIAN);

        int cType = msqWrapper.getInt();
        int bUserId = msqWrapper.getInt();
        byte[] message = new byte[bMsq.length-8];
        msqWrapper.get(message);


        return (new Packet(bSrc,bPktId,cType,bUserId, new String(message)));
    }
}
