import java.util.concurrent.atomic.AtomicInteger;

import network.PackageProcessor;
import network.Package;

public class PackageGenerator {
    static  AtomicInteger counter = new AtomicInteger(1);

    public static byte[] generateCorrectPackage() throws Exception {
        int c = counter.getAndIncrement();
        return PackageProcessor.encode(new Package((byte)c,c,c,c,"Package #"+c));
    }
    public static byte[] generateIncorrectPackage() throws Exception {
        int c = 1;
        byte[] failPack =  PackageProcessor.encode(new Package((byte)c,c,c,c,"Package #"+c));
        int exceptionChoice = (int) (10*Math.random() ) %3;
        if(exceptionChoice== 0)failPack[0] = 0; //wrong magic byte
        else if(exceptionChoice == 1) ++failPack[14]; //wrong wCrc1
        else if(exceptionChoice == 2) ++failPack[failPack.length-1]; //wrong wCrc2
        return failPack;
    }
}
