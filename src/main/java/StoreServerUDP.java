
import clases.Processor;
import network.impl.UDPNetwork;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StoreServerUDP {

    public static void main(String[] args) {
        Processor.initService();
        ExecutorService threadPool = Executors.newFixedThreadPool(12);
        try {
            UDPNetwork network = new UDPNetwork();
            System.out.println("StoreServerUDP running via " + network + " connection");
            network.listen();
            for(int i = 0; i<12;++i) {
                threadPool.submit(() -> {
                    try {
                        while (true){
                            network.receive();
                        Thread.sleep(2100);}
                    } finally {
                            network.close();
                    }

                });
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                threadPool.shutdown();
                while (!threadPool.isTerminated())
                    threadPool.awaitTermination(5, TimeUnit.SECONDS);

                Processor.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


}