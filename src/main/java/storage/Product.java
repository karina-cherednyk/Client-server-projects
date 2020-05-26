package storage;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Product {
    private final String name;
    private AtomicInteger quantity;
    private AtomicInteger price;


    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }
    public int getPrice() {
        return price.get();
    }

    public void setPrice(int price) {
        this.price.set(price);
    }


    public synchronized void addQuantity(int quantity) {
        this.quantity.addAndGet(quantity);
    }
    public synchronized void reduceQuantity(int quantity) {
        if(quantity>this.quantity.get()) throw new IllegalArgumentException("Incorrect quantity");
        this.quantity.addAndGet(-quantity);
    }
}
