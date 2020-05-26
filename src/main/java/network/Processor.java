package network;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor implements Runnable{
    private static ExecutorService threadPool = Executors.newFixedThreadPool(6);
    private Message message;
    private static AtomicInteger counter = new AtomicInteger(0);

    private Processor(Message message) {
        this.message = message;
    }

    public static void process(Message message){
        threadPool.submit( new Processor(message) );
    }

    public static  void shutdown(){
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
            threadPool.shutdownNow();
        }
    }
    public static void initService(){
        if(threadPool.isTerminated()) threadPool = Executors.newFixedThreadPool(6);
    }

    @Override
    public void run() {
        System.out.println("Message "+message+" was received");
        int c = counter.getAndIncrement();
        Package pack = new Package((byte)c,c,c,c,"OK");
        try {
            byte[] bytePack = PackageProcessor.encode(pack);
            System.out.println("Sending response "+pack.getBmsq()+" to "+InetAddress.getLocalHost());
            TCPNetwork.getInstance().sendMessage(bytePack, InetAddress.getLocalHost());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
