package entities;

import com.google.common.primitives.UnsignedLong;

import java.net.InetAddress;
import java.util.Objects;

public final class Package{
    private byte bSrc;
    private UnsignedLong bPktId;
    private Message bmsq;

    private InetAddress clientInetAddress;
    private int clientPort;


    public Package(byte bSrc, UnsignedLong bPktId, int cType, int bUserId, String message) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bmsq = new Message(cType,bUserId,message);
    }
    public Package(byte bSrc, UnsignedLong bPktId, int cType, int bUserId, String message,  InetAddress clientInetAddress,int clientPort) {
        this(bSrc,bPktId,cType,bUserId,message);
        this.clientInetAddress=clientInetAddress;
        this.clientPort=clientPort;
    }

    //unique number of client app
    public byte getbSrc() {
        return bSrc;
    }
    //package number
    public UnsignedLong getbPktId() {
        return bPktId;
    }
    //command code
    public int getcType() {
        return bmsq.getcType();
    }
    //user id
    public int getbUserId() {
        return bmsq.getbUserId();
    }
    //useful message
    public String getMessage() {
        return bmsq.getMessage();
    }

    public Message getBmsq() {
        return bmsq;
    }

    public InetAddress getClientInetAddress() {
        return clientInetAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientInetAddress(InetAddress clientInetAddress) {
        this.clientInetAddress = clientInetAddress;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return bSrc == aPackage.bSrc &&
                bPktId.equals(aPackage.bPktId) &&
                bmsq.equals(aPackage.bmsq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bSrc, bPktId, bmsq);
    }

    @Override
    public String toString() {
        return "Package{" +
                "bSrc=" + bSrc +
                ", bPktId=" + bPktId +
                ", bmsq=" + bmsq +
                '}';
    }
}