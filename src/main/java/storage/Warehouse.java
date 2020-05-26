package storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Warehouse {

    enum Command{
        getQuantity, reduceQuantity, addQuantity, addGroup, addProduct, setPrice
    }

    private List<ProductGroup> groups = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String,ProductGroup> map = new ConcurrentHashMap<>();

    public int getQuantity(String productName){
        if(!map.containsKey(productName)) return 0;

        return map.get(productName).getQuantity(productName);
    }
    public boolean reduce(String productName, int quantity){
        if(!map.containsKey(productName)) return false;
        map.get(productName).reduce(productName,quantity);
        return true;
    }
    public boolean add(String productName, int quantity){
        if(!map.containsKey(productName)) return false;
        map.get(productName).add(productName,quantity);
        return true;
    }
    public boolean addGroup(String groupName){
        if(groups.contains(groupName)) return false;

        groups.add(new ProductGroup(groupName));
        return true;
    }
    public boolean addProductToGroup(String productName, String groupName){
        if(groups.contains(groupName) || map.containsKey(productName)) return false;

        map.put(productName, groups.get(groups.indexOf(groupName)));

        return true;
    }
    public boolean setPrice(String productName, int price){
        if(!map.containsKey(productName)) return false;
        map.get(productName).setPrice(productName,price);
        return true;
    }
}
