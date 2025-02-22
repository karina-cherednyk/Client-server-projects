package network;

import java.util.Objects;

public final class Package{
    private byte bSrc;
    private long bPktId;
    private Message bmsq;


    public Package(byte bSrc, long bPktId, int cType, int bUserId, String message) {
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.bmsq = new Message(cType,bUserId,message);
    }

    //unique number of client app
    public byte getbSrc() {
        return bSrc;
    }
    //package number
    public long getbPktId() {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return bSrc == aPackage.bSrc &&
                bPktId == aPackage.bPktId &&
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