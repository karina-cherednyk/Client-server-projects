package storage;

import utils.AtomicDouble;


import java.util.concurrent.atomic.AtomicInteger;

public class Product {
    private final String name;
    private AtomicInteger quantity;
    private AtomicDouble price;
    private final int id;

    public Product(int id, String name, double price) {
        this(id,name,price,0);
    }
    public Product(int id, String name, double price, int quantity) {
        this.name = name;
        this.id = id;
        this.price = new AtomicDouble(price);
        this.quantity = new AtomicInteger(quantity);
    }
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }
    public double getPrice() {
        return price.get();
    }

    public void setPrice(int price) {
        this.price.set(price);
    }

    public int getId(){ return id;}


    public synchronized void addQuantity(int quantity) {
        this.quantity.addAndGet(quantity);
    }
    public synchronized void reduceQuantity(int quantity) {
        if(quantity>this.quantity.get()) throw new IllegalArgumentException("Incorrect quantity");
        this.quantity.addAndGet(-quantity);
    }
}
