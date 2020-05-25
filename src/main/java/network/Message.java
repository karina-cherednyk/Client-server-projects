package network;

import java.util.Objects;

public class Message {
    private final int cType, bUserId;
    private final String message;

    Message(int cType, int bUserId, String message) {
        this.cType = cType;
        this.bUserId = bUserId;
        this.message = message;
    }

    int getcType() {
        return cType;
    }

    int getbUserId() {
        return bUserId;
    }

    String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return cType == message1.cType &&
                bUserId == message1.bUserId &&
                Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cType, bUserId, message);
    }

    @Override
    public String toString() {
        return "Message{" +
                "cType=" + cType +
                ", bUserId=" + bUserId +
                ", message='" + message + '\'' +
                '}';
    }
}
