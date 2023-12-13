package sandclub.beeradvisor.model;

import java.util.List;

public class BeerResponse {
    private List<Beer> beerList;

    public BeerResponse() {
    }

    public BeerResponse(List<Beer> beerList) {
        this.beerList = beerList;
    }

    public List<Beer> getBeerList() {
        return beerList;
    }

    public void setBeerList(List<Beer> beerList) {
        this.beerList = beerList;
    }
}
