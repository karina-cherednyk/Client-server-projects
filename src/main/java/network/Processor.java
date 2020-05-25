package network;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor implements Runnable{
    private static AtomicInteger c = new AtomicInteger(0); //counter
    private static ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private Processor(Package pack) {
        this.pack = pack;
    }

    public static void process(Package pack){
        threadPool.submit( new Processor(pack) );
    }

    public static void shutDown(){
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    private Package pack;
    @Override
    public void run() {
        System.out.println("Message has came: "+ pack.getBmsq());
        try {
            InetAddress addr = InetAddress.getLocalHost();
            int i = c.getAndIncrement();
            Package responce = new Package((byte)i,i,i,i, "Sending responce...");
            byte[] messageToSend = BlowfishCipherProcessor.getInstance().encrypt(responce);

            TCPNetwork.getInstance().sendMessage(messageToSend,addr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
