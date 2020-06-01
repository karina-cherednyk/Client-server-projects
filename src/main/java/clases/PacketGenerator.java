package clases;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.primitives.UnsignedLong;
import entities.Packet;

public class PacketGenerator {
    static  AtomicInteger counter = new AtomicInteger(1);

    public static byte[] generateCorrectPackage() throws Exception {
        int c = counter.getAndIncrement();
        return PacketProcessor.encode(new Packet((byte)c, UnsignedLong.valueOf(String.valueOf(c)),c,c,"Packet #"+c));
    }
    public static Packet generateCorrect()  {
        int c = counter.getAndIncrement();
        return (new Packet((byte)c, UnsignedLong.valueOf(String.valueOf(c)),c,c,"Packet #"+c));

    }
    public static byte[] generateIncorrectPackage() throws Exception {
        int c = 1;
        byte[] failPack =  PacketProcessor.encode(new Packet((byte)c,UnsignedLong.valueOf(String.valueOf(c)),c,c,"Packet #"+c));
        int exceptionChoice = (int) (10*Math.random() ) %3;
        if(exceptionChoice== 0)failPack[0] = 0; //wrong magic byte
        else if(exceptionChoice == 1) ++failPack[14]; //wrong wCrc1
        else if(exceptionChoice == 2) ++failPack[failPack.length-1]; //wrong wCrc2
        return failPack;
    }
}
