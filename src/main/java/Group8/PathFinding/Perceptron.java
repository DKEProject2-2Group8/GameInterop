package Group8.PathFinding;
import java.lang.Math;

public class Perceptron {

    public double Activation(double Z){

        double sigma = 1 / (1 + Math.exp(-10*(Z-0.5)) );

        return sigma;
    }

}