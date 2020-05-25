import network.Processor;
import network.TCPNetwork;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(6);

        for(int i=0; i<2 ; ++i){
            threadPool.submit(()->{
                TCPNetwork.getInstance().receiveMessage();
            });
        }
        //TCPNetwork.getInstance().receiveMessage();
        Processor.shutDown();
        threadPool.shutdown();
//        try {
//            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
//                threadPool.shutdownNow();
//            }
//        } catch (InterruptedException ex) {
//            threadPool.shutdownNow();
//            Thread.currentThread().interrupt();
//        }

        System.out.println("Network simulation finished");
    }
}
