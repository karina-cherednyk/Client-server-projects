package network;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private static AtomicInteger counter = new AtomicInteger(0);


    public static void process(Message message) {
        int c = counter.getAndIncrement();
        Package pack = new Package((byte) c, c, c, c, "OK");
        process(pack);
    }

    private static void process(Package pack) {
        threadPool.submit(() -> {

            try {
                byte[] bytePack = PackageProcessor.encode(pack);
                System.out.println("Sending response " + pack.getBmsq() + " to " + InetAddress.getLocalHost());
                TCPNetwork.getInstance().sendMessage(bytePack, InetAddress.getLocalHost());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void processFail() {
        int c = counter.getAndIncrement();
        Package pack = new Package((byte) c, c, c, c, "FAIL");
        process(pack);
    }

    public static void shutdown() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            threadPool.shutdownNow();
        }
    }
    public static void initService(){
        if(threadPool.isTerminated()) threadPool = Executors.newFixedThreadPool(6);

    }
}