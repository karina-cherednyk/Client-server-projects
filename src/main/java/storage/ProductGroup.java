package storage;

import java.util.HashMap;
import java.util.Objects;


public class ProductGroup {
    private final String groupName;
    private HashMap<String, Product> productList = new HashMap<>();

    public ProductGroup(String groupName) {
        this.groupName = groupName;
    }

    public void setPrice(String productName,int price){
        productList.get(productName).setPrice(price);
    }
    public int getQuantity(String productName){
        return productList.get(productName).getQuantity();
    }
    public void add(String productName, int quantity){
        productList.get(productName).addQuantity(quantity);
    }
    public void reduce(String productName, int quantity){
        productList.get(productName).reduceQuantity(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof  String) return groupName.equals(o);
        if (o == null || getClass() != o.getClass()) return false;
        ProductGroup that = (ProductGroup) o;
        return Objects.equals(productList, that.productList);
    }

    public String getGroupName() {
        return groupName;
    }
}
