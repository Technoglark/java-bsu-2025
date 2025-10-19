import java.util.ArrayList;

public class ElectronicsLine extends ProductionLine<ElectronicProduct>{

    public ElectronicsLine(String lineId, ArrayList<ElectronicProduct> products, double efficiency){
        super(lineId, products, efficiency);
    }

    @Override
    public boolean canProduce(Product product){
        return product instanceof ElectronicProduct;
    }
}
