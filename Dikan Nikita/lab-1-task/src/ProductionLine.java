import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ProductionLine<T extends Product> {
    private final String lineId;
    private final List<T> products;
    private final double efficiency;

    public ProductionLine(String lineId, List<T> products, double efficiency){
        if (lineId == null || lineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Line ID не может быть null или пустым");
        }
        if (products == null) {
            throw new IllegalArgumentException("Products list не может быть null");
        }
        if (efficiency < 0.0 || efficiency > 1.0) {
            throw new IllegalArgumentException("Efficiency должен быть в промежутке [0, 1]");
        }
        this.lineId = lineId;
        this.products = new ArrayList<>(products);
        this.efficiency = efficiency;
    }

    public String getLineId(){
        return this.lineId;
    }
    public List<T> getProducts(){
        return Collections.unmodifiableList(this.products);
    }
    public double getEfficiency(){
        return this.efficiency;
    }

    public void addProduct(Product product){
        if (!canProduce(product)){
            throw new IllegalArgumentException(
                    "Линия " + lineId + " не может производить продукт " + product.getName() +
                            " (тип " + product.getCategory() + ")"
            );
        }
        @SuppressWarnings("unchecked")
        T typedProduct = (T) product;
        this.products.add(typedProduct);
    }

    public abstract boolean canProduce(Product product);
}
