import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {
    public static void main(String[] args){

        ArrayList<ElectronicProduct> eproducts = new ArrayList<>();
        eproducts.add(new ElectronicProduct("000", "computer", 100));
        eproducts.add(new ElectronicProduct("001", "blender", 102));
        ElectronicsLine eline = new ElectronicsLine("1", eproducts, 0.2);



        ArrayList<MechanicalProduct> mproducts = new ArrayList<>();
        mproducts.add(new MechanicalProduct("010", "machine", 104));
        mproducts.add(new MechanicalProduct("011", "wheel", 105));
        mproducts.add(new MechanicalProduct("012", "mechanism", 106));
        MechanicalsLine mline = new MechanicalsLine("222", mproducts, 0.6);




        ArrayList<ChemicalProduct> cproducts = new ArrayList<>();
        cproducts.add(new ChemicalProduct("020", "radium", 107));
        cproducts.add(new ChemicalProduct("021", "hidrargirum", 108));
        ChemicalsLine cline = new ChemicalsLine("3", cproducts, 0.8);

        // Тесты на принадлежность к линии производства
        System.out.println("Может ли ChemicalsLine производить ChemicalProduct: " + cline.canProduce(cproducts.get(0)));
        System.out.println("Может ли ChemicalsLine производить MechanicalProduct: " + cline.canProduce(mproducts.get(0)));
        System.out.println("Может ли ChemicalsLine производить ElectronicProduct: " + cline.canProduce(eproducts.get(0)));

        // Тесты имен категорий
        MechanicalProduct mproduct = mproducts.get(0);
        ElectronicProduct eproduct = eproducts.get(0);
        ChemicalProduct cproduct = cproducts.get(0);
        System.out.println("Имя MechanicalProduct объекта: " + mproduct.getCategory());
        System.out.println("Имя ElectronicProduct объекта: " + eproduct.getCategory());
        System.out.println("Имя ChemicalProduct объекта: " + cproduct.getCategory());

        // Тест addLine в ProdAnalyzer
        List<ProductionLine<? extends Product>> lines = new ArrayList<>();
        lines.add(eline);
        lines.add(cline);
        ProdAnalyzer productAnalyzer = new ProdAnalyzer(lines);
        productAnalyzer.addLine(mline);

        // Тест ProdAnalyzer
        List<String> efficientLines = productAnalyzer.getHighEfficiencyLines(0.5);
        Map<String, Long> counts = productAnalyzer.countProductsByCategory();
        Optional<ProductionLine<? extends Product>> mostLoadedLine = productAnalyzer.findMostLoadedLine();
        List<Product> allProducts = productAnalyzer.getAllProductsFromLines();

        System.out.println("Efficient lines: " + efficientLines.toString());
        System.out.println("Categories-counts: " + counts.toString());
        mostLoadedLine.ifPresent(line -> {
            System.out.println("Самая загруженная линия: id=" + line.getLineId());
        });
        System.out.println("Все продукты: " + allProducts.toString());
        System.out.println("Общее время производства всех продуктов: " + productAnalyzer.calculateTotalProductionTime());

        // Попробуем добавить невалидный продукт в линию
        try {
            cline.addProduct(mproduct);
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        // Попробуем добавить адекватный продукт в линию

        try{
            cline.addProduct(cproduct);
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Новый состав продуктов в ChemicalLine объекте: " + cline.getProducts().toString());

    }
}
