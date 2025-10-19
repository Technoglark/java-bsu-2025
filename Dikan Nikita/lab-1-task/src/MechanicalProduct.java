public class MechanicalProduct extends Product{

    public MechanicalProduct(String id, String name, int prodTime){
        super(id, name, prodTime);
    }

    @Override
    public String getCategory(){
        return "Mechanical";
    }
}
