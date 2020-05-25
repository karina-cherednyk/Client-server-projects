package storage;

import java.util.Objects;

public class Product {
    private final String name;
    private int quantity;
    private int price;


    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public synchronized void addQuantity(int quantity) {
        this.quantity += quantity;
    }
    public synchronized void reduceQuantity(int quantity) {
        if(quantity>this.quantity) throw new IllegalArgumentException("Incorrect quantity");
        this.quantity -= quantity;
    }
}
