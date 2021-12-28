import java.util.Arrays;
import java.util.Random;

public class Genotype {
    private int[] genotype;

    public Genotype(){
        Random rand = new Random();
        this.genotype = new int[32];
        for(int i=0; i < 32; i++){
            this.genotype[i] = rand.nextInt(8);
        }
        Arrays.sort(this.genotype);
    }

    public Genotype(int leftRight, int howMany, int[] weak, int[] strong){
        int[] left = strong;
        int[] right = weak;
        if(leftRight != 0){
            howMany = 32-howMany;
            left = weak;
            right = strong;
        }
        int[] res = new int[32];
        for(int j=0; j < 32; j++){
            if(j<howMany){
                res[j] = left[j];
            }
            else{
                res[j] = right[j];
            }
        }
        Arrays.sort(res);
        this.genotype = res;
    }

    public int[] getGenotype() {
        return genotype;
    }
}
