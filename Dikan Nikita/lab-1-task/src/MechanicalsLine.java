import java.util.ArrayList;

public class MechanicalsLine extends ProductionLine<MechanicalProduct>{

    public MechanicalsLine(String lineId, ArrayList<MechanicalProduct> products, double efficiency){
        super(lineId, products, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof MechanicalProduct;
    }
}
