package sandclub.beeradvisor.model;

import java.util.List;

public class BeerFoodPairing {
    private String food;

    public BeerFoodPairing(String food) {
        this.food = food;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    @Override
    public String toString() {
        return "BeerFoodPairing{" +
                "food='" + food + '\'' +
                '}';
    }
}
