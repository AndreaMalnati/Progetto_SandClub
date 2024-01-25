package sandclub.beeradvisor.source;

import java.util.List;

import sandclub.beeradvisor.model.Beer;

public abstract class BaseBeerLocalDataSource { //Classe base per la lettura in locale

    protected BeerCallback beerCallback;

    public void setBeerCallback(BeerCallback beerCallback) {
        this.beerCallback = beerCallback;
    }

    public abstract void getBeer(); //Restituisce tutte le birre
    //public abstract void getFavoriteBeers(); //Restituisce le birre preferite
    //public abstract void updateBeer(Beer beer); //Aggiorna stato birra da preferito o no

    //public abstract void deleteFavoriteBeer(); //Cancella birra preferita
    public abstract void insertBeer(List<Beer> beerList); //Inserisce birre dentro database
}
