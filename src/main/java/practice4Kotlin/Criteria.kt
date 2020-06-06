package practice4Kotlin

data class Criteria(var like: String?, var priceFrom: Double?, var priceTo: Double?){
    open fun filter(): String{
        if(listOfNotNull(like, priceFrom, priceTo).isEmpty()) return "";
        val  conditions = mutableListOf<String>();
            like?.let{ conditions += "name like $like" }
            if( priceFrom!=null && priceTo != null )
                conditions += " price between $priceFrom and $priceTo";
            else {
                priceFrom?.let { conditions += "price >= $priceFrom" };
                priceTo?.let { conditions += "price <= $priceTo" };
            }

        return  conditions.joinToString( prefix = "filter ", separator = " and " )
    }
}

