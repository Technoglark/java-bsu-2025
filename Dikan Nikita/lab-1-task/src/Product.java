public abstract class Product {
    private final String id;
    private final String name;
    private final int productionTime;

    public Product(String id, String name, int prodTime){
        this.id = id;
        this.name = name;
        this.productionTime = prodTime;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getProductionTime(){
        return productionTime;
    }

    public abstract String getCategory();
}

