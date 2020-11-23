package online.model;
import java.util.ArrayList;
public class Embedding {
    ArrayList<Float> vec;
    public Embedding(Embedding emb) {
        this.vec = emb.vec;
    }
    public Embedding() {
        vec = new ArrayList<>();
    }
    public void add_element (Float temp){
        vec.add(temp);
    }
    public ArrayList<Float> Get_vec() {
        return vec;
    }
    public void set_vec(ArrayList<Float> vec) {
        this.vec = vec;
    }
    public double calculated_similarity(Embedding other_emb){
        double dot_product = 0;
        double denominateA = 0;
        double denominateB = 0;
        for (int i = 0; i < this.vec.size(); ++ i) {
            dot_product += this.vec.get(i) * other_emb.Get_vec().get(i);
            denominateA += this.vec.get(i) * this.vec.get(i);
            denominateB += other_emb.Get_vec().get(i) * other_emb.Get_vec().get(i);
        }
        return dot_product / Math.sqrt(denominateA) * Math.sqrt(denominateB);
    }
}
