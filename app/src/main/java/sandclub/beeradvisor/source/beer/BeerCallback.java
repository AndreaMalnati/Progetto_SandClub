package sandclub.beeradvisor.source.beer;

import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;

public interface BeerCallback {
    void onSuccessFromRemote(BeerApiResponse beerApiResponse, long lastUpdate); //Chiamata back-end OK
    void onFailureFromRemote(Exception exception); //Chiamata back-end NO
    void onSuccessFromLocal(List<Beer> beerList); //Lettura/scrittura DB OK
    void onFailureFromLocal(Exception exception); //Lettura/scrittura DB NO
    void onNewsFavoriteStatusChanged(Beer beer, List<Beer> favoriteBeer); //Aggiornamento di una singola birra
    void onNewsFavoriteStatusChanged(List<Beer> beer); //Aggiornamento di tutte la lista birre
    void onDeleteFavoriteNewsSuccess(List<Beer> favoriteBeer); //Listener per la cancellazione di tutte le birre preferite


}
