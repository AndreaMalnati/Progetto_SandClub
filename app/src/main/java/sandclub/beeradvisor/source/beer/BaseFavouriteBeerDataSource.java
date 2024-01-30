package sandclub.beeradvisor.source.beer;

import java.util.List;

import sandclub.beeradvisor.model.Beer;

public abstract class BaseFavouriteBeerDataSource {
    protected BeerCallback beerCallback;

    public void setBeerCallback(BeerCallback beerCallback) {
        this.beerCallback = beerCallback;
    }

    public abstract void getFavoriteBeer(String idToken);

    public abstract void addFavoriteBeer(Beer beer, String idToken);

    public abstract void synchronizeFavoriteBeer(List<Beer> notSynchronizedBeerList, String idToken);
    public abstract void deleteFavoriteBeer(Beer beer, String idToken);
    public abstract void deleteAllFavoriteBeer();


}
