import network.*;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Lab02Test {

    @Test
    public void withoutExceptions()  {
        try {
            Processor.initService(); TCPNetwork.initService();
            ExecutorService threadPool = Executors.newFixedThreadPool(12);

            for (int i = 0; i < 5; ++i) {
                threadPool.submit(() -> {
                    try {
                        byte[] pack = PackageGenerator.generateCorrectPackage();
                        TCPNetwork.getInstance().receiveMessage(pack);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);

            TCPNetwork.shutdownReceiver();
            Processor.shutdown();
            TCPNetwork.shutdownSender();

        }catch (Throwable t){
            fail(t.getMessage());
        }
    }

    @Test
    public void twoPackagesBroken() throws InterruptedException {
        Processor.initService(); TCPNetwork.initService();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; ++i) {
            int finalI = i;
            threadPool.submit(() -> {
                try {
                    byte[] pack = PackageGenerator.generateIncorrectPackage();
                    TCPNetwork.getInstance().receiveMessage(pack);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        threadPool.shutdown();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);


    }


}
