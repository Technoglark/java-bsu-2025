public class ElectronicProduct extends Product {
    public ElectronicProduct(String id, String name, int prodTime) {
        super(id, name, prodTime);
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }
}
