import java.util.ArrayList;

public class ChemicalsLine extends ProductionLine<ChemicalProduct>{

    public ChemicalsLine(String lineId, ArrayList<ChemicalProduct> products, double efficiency){
        super(lineId, products, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ChemicalProduct;
    }
}
