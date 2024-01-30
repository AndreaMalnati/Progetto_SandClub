package sandclub.beeradvisor.source.beer;

import java.util.List;

import sandclub.beeradvisor.model.Beer;

public abstract class BaseFavouriteBeerDataSource {
    protected BeerCallback beerCallback;

    public void setBeerCallback(BeerCallback beerCallback) {
        this.beerCallback = beerCallback;
    }

    public abstract void getFavoriteBeer();
    public abstract void addFavoriteBeer(Beer beer);
    public abstract void synchronizeFavoriteBeer(List<Beer> notSynchronizedBeerList);
    public abstract void deleteFavoriteBeer(Beer beer);
    public abstract void deleteAllFavoriteBeer();


}
