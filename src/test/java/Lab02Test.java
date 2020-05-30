import clases.PackageGenerator;
import clases.Processor;
import network.impl.TCPNetworkA;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class Lab02Test {

    @Test
    public void withoutExceptions()  {
        try {
            Processor.initService(); TCPNetworkA.initService();
            ExecutorService threadPool = Executors.newFixedThreadPool(12);

            for (int i = 0; i < 5; ++i) {
                threadPool.submit(() -> {
                    try {
                        byte[] pack = PackageGenerator.generateCorrectPackage();
                        TCPNetworkA.getInstance().receiveMessage(pack);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.SECONDS);

            TCPNetworkA.shutdownReceiver();
            Processor.shutdown();
            TCPNetworkA.shutdownSender();

        }catch (Throwable t){
            fail(t.getMessage());
        }
    }

    @Test
    public void twoPackagesBroken() throws InterruptedException {
        Processor.initService(); TCPNetworkA.initService();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; ++i) {
            int finalI = i;
            threadPool.submit(() -> {
                try {
                    byte[] pack = PackageGenerator.generateIncorrectPackage();
                    TCPNetworkA.getInstance().receiveMessage(pack);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        threadPool.shutdown();
        threadPool.awaitTermination(5, TimeUnit.SECONDS);


    }


}
