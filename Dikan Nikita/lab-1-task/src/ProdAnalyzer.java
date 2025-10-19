import java.util.*;
import java.util.stream.Collectors;

public class ProdAnalyzer {

    private final List<ProductionLine<? extends Product>> lines;

    public ProdAnalyzer(List<ProductionLine<? extends Product>> lines){
        this.lines = lines;
    }

    public ProdAnalyzer(){
        this.lines = new ArrayList<>();
    }

    public void addLine(ProductionLine<? extends Product> line){
        this.lines.add(line);
    }


    List<ProductionLine<? extends Product>> getAllLines(){
        return this.lines;
    }

    List<String> getHighEfficiencyLines(double threshold){
        return this.lines.stream().
                filter(p -> p.getEfficiency() > threshold).
                map(ProductionLine::getLineId).
                collect(Collectors.toList());
    }

    Map<String, Long> countProductsByCategory(){
        return this.lines.stream()
                .flatMap(line -> line.getProducts().stream())
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }

    Optional<ProductionLine<? extends Product>> findMostLoadedLine(){
        return this.lines.stream()
                .max(Comparator.comparingInt(line -> line.getProducts().size()));
    }

    List<Product> getAllProductsFromLines(){
        return this.lines.stream()
                .flatMap(line -> line.getProducts().stream())
                .collect(Collectors.toList());
    }

    public int calculateTotalProductionTime() {
        return this.lines.stream()
                .flatMap(line -> line.getProducts().stream())
                .mapToInt(Product::getProductionTime)
                .sum();
    }
}
