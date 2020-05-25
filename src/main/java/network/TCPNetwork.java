package network;


import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPNetwork implements Network {
    private static TCPNetwork instance;
    private static AtomicInteger c = new AtomicInteger(10);
    public static TCPNetwork getInstance(){
        if(instance == null) instance = new TCPNetwork();
        return instance;
    }
    private TCPNetwork(){}
    @Override
    public void receiveMessage() {
        int i = c.getAndIncrement();
        Package pack = new Package((byte)i,i,i,i,"sending test message");
        Processor.process(pack);
    }

    @Override
    public synchronized void sendMessage(byte[] mess, InetAddress target) throws Exception {
        System.out.print("Sending package to: "+target+" , ");
        BlowfishCipherProcessor.getInstance().decrypt(mess);
    }


}
