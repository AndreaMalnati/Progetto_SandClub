package sandclub.beeradvisor.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;

public class BeerViewModel extends ViewModel {
    private static final String TAG = BeerViewModel.class.getSimpleName();

    private final IBeerRepositoryWithLiveData beerRepositoryWithLiveData;
    private MutableLiveData<Result> beerListLiveData;
    private MutableLiveData<Result> favoriteBeerListLiveData;



    public BeerViewModel(IBeerRepositoryWithLiveData iBeerRepositoryWithLiveData) {
        this.beerRepositoryWithLiveData = iBeerRepositoryWithLiveData;
    }

    /**
     * Returns the LiveData object associated with the
     * news list to the Fragment/Activity.
     * @return The LiveData object associated with the news list.
     */
    public MutableLiveData<Result> getBeer(long lastUpdate) {
        if (beerListLiveData == null) {
            fetchBeer(lastUpdate);
        }
        return beerListLiveData;
    }
    public MutableLiveData<Result> getFavoriteBeerLiveData(boolean isFirstLoading) {
        if (favoriteBeerListLiveData == null) {
            getFavoriteBeer(isFirstLoading);
        }
        return favoriteBeerListLiveData;
    }
    public void fetchBeer(long lastUpdate) {
        beerListLiveData = beerRepositoryWithLiveData.fetchAllBeer(lastUpdate);
    }

    private void getFavoriteBeer(boolean isFirstLoading){
        favoriteBeerListLiveData = beerRepositoryWithLiveData.getFavoriteBeer(isFirstLoading);
    }
    public void updateBeer(Beer beer) {
        beerRepositoryWithLiveData.updateBeer(beer);
    }

}
