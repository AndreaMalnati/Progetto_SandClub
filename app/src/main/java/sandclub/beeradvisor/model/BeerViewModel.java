package sandclub.beeradvisor.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import sandclub.beeradvisor.repository.IBeerRepositoryWithLiveData;

public class BeerViewModel extends ViewModel {
    private static final String TAG = BeerViewModel.class.getSimpleName();

    private final IBeerRepositoryWithLiveData beerRepositoryWithLiveData;
    private MutableLiveData<Result> beerListLiveData;


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

    public void fetchBeer(long lastUpdate) {
        beerListLiveData = beerRepositoryWithLiveData.fetchAllBeer(lastUpdate);
    }

}
