package practice4;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Criteria {
    private String like;
    private String fixedName;
    private double priceFrom = -1;
    private double priceTo = -1;
    private double fixedPrice = -1;


    public Criteria(){}
    public void setLike(String like){
        this.like = like;
    }
    public void setFixedName(String name){
        this.fixedName = name;
    }

    public void setPriceFrom(double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public void setPriceTo(double priceTo) {
        this.priceTo = priceTo;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }
    public String getFilterQuery(){
        if(like==null && fixedName==null && priceFrom == -1 && priceTo == -1 && fixedPrice == -1) return  "";
        LinkedList<String> list = new LinkedList<>();
        if(fixedName != null) list.add("name = "+fixedName);
        else if(like != null) list.add( "name like "+"'.*"+like+".*'");

        if(priceFrom!= -1 && priceTo!= -1) list.add(" price between "+priceFrom+" and "+priceTo);
        else if(priceFrom!= -1) list.add("price >= "+priceFrom);
        else if(priceTo!= -1) list.add("price <= "+priceTo);

        return  "where "+ String.join(" and ");
    }
}
