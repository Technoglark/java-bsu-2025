public class ChemicalProduct extends Product{

    public ChemicalProduct(String id, String name, int prodTime){
        super(id, name, prodTime);
    }

    @Override
    public String getCategory(){
        return "Chemical";
    }
}
