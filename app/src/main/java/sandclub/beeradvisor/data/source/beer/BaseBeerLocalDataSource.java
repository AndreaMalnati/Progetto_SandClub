package sandclub.beeradvisor.data.source.beer;

import java.util.List;
import java.util.Set;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;

public abstract class BaseBeerLocalDataSource { //Classe base per la lettura in locale

    protected BeerCallback beerCallback;

    public void setBeerCallback(BeerCallback beerCallback) {
        this.beerCallback = beerCallback;
    }

    public abstract void getBeer(); //Restituisce tutte le birre
    //public abstract void getFavoriteBeers(); //Restituisce le birre preferite
    //public abstract void updateBeer(Beer beer); //Aggiorna stato birra da preferito o no

    //public abstract void deleteFavoriteBeer(); //Cancella birra preferita
    public abstract void insertBeer(BeerApiResponse beerApiResponse); //Inserisce birre dentro database
    public abstract void insertBeer(List<Beer> beerList); //Inserisce birre dentro database

    public abstract void updateBeer(Beer beer);
    public abstract void getFavoriteBeer();

    public abstract Beer getBeerId(Set<Integer> ids);

    public abstract  void getFilteredBeer(String filter);


}
