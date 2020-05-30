package clases;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.primitives.UnsignedLong;
import entities.Package;

public class PackageGenerator {
    static  AtomicInteger counter = new AtomicInteger(1);

    public static byte[] generateCorrectPackage() throws Exception {
        int c = counter.getAndIncrement();
        return PackageProcessor.encode(new Package((byte)c, UnsignedLong.valueOf(String.valueOf(c)),c,c,"Package #"+c));
    }
    public static Package generateCorrect() throws Exception {
        int c = counter.getAndIncrement();
        byte [] pack = PackageProcessor.encode(new Package((byte)c, UnsignedLong.valueOf(String.valueOf(c)),c,c,"Package #"+c));

        byte bSrc = pack[1];
        UnsignedLong bPktId = UnsignedLong.asUnsigned(ByteBuffer.wrap(pack,2,8 )
                .order(ByteOrder.BIG_ENDIAN)
                .getLong());
        int wLen =  ByteBuffer.wrap(pack,10,4 )
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        byte[] bMsq = new byte[wLen];
        System.arraycopy(pack, 16, bMsq,0,wLen);
        ByteBuffer msqWrapper = ByteBuffer.wrap(bMsq).order(ByteOrder.BIG_ENDIAN);

        int cType = msqWrapper.getInt();
        int bUserId = msqWrapper.getInt();
        byte[] message = new byte[bMsq.length-8];
        msqWrapper.get(message);
        return (new Package(bSrc,bPktId,cType,bUserId, new String(message)));
    }
    public static byte[] generateIncorrectPackage() throws Exception {
        int c = 1;
        byte[] failPack =  PackageProcessor.encode(new Package((byte)c,UnsignedLong.valueOf(String.valueOf(c)),c,c,"Package #"+c));
        int exceptionChoice = (int) (10*Math.random() ) %3;
        if(exceptionChoice== 0)failPack[0] = 0; //wrong magic byte
        else if(exceptionChoice == 1) ++failPack[14]; //wrong wCrc1
        else if(exceptionChoice == 2) ++failPack[failPack.length-1]; //wrong wCrc2
        return failPack;
    }
}
