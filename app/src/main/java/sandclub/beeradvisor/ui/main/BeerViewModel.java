package sandclub.beeradvisor.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Set;

import sandclub.beeradvisor.data.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.Result;

public class BeerViewModel extends ViewModel {
    private static final String TAG = BeerViewModel.class.getSimpleName();

    private final IBeerRepositoryWithLiveData beerRepositoryWithLiveData;
    private MutableLiveData<Result> beerListLiveData;
    private MutableLiveData<Result> favoriteBeerListLiveData;
    private MutableLiveData<Result> singleBeerLiveData;
    private MutableLiveData<Result> lastDrunkBeerLiveData;
    private boolean isFirstLoading;




    public BeerViewModel(IBeerRepositoryWithLiveData iBeerRepositoryWithLiveData) {
        this.beerRepositoryWithLiveData = iBeerRepositoryWithLiveData;
        this.isFirstLoading = true;
    }





    public MutableLiveData<Result> getBeer(long lastUpdate) {
        if (beerListLiveData == null) {
            fetchBeer(lastUpdate);
        }
        return beerListLiveData;
    }

    public MutableLiveData<Result> getBeerFilteredMutableLiveData(String filter) {
        getFilteredBeer(filter);
        return beerListLiveData;
    }

    public MutableLiveData<Result> getFavoriteBeerLiveData(boolean isFirstLoading) {
        if (favoriteBeerListLiveData == null) {
            getFavoriteBeer(isFirstLoading);
        }
        return favoriteBeerListLiveData;
    }



    public MutableLiveData<Result> getBeerById(Set<Integer> ids) {
            getBeerId(ids);
        return singleBeerLiveData;
    }


    public void getFilteredBeer(String filter) {
        beerListLiveData = beerRepositoryWithLiveData.getFilteredBeer(filter);
    }
    public void getBeerId(Set<Integer> ids) {
        singleBeerLiveData = beerRepositoryWithLiveData.getBeerId(ids);
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

    public IBeerRepositoryWithLiveData getBeerRepositoryWithLiveData() {
        return beerRepositoryWithLiveData;
    }

    public MutableLiveData<Result> getBeerListLiveData() {
        return beerListLiveData;
    }

    public void setBeerListLiveData(MutableLiveData<Result> beerListLiveData) {
        this.beerListLiveData = beerListLiveData;
    }

    public MutableLiveData<Result> getFavoriteBeerListLiveData() {
        return favoriteBeerListLiveData;
    }

    public void setFavoriteBeerListLiveData(MutableLiveData<Result> favoriteBeerListLiveData) {
        this.favoriteBeerListLiveData = favoriteBeerListLiveData;
    }

    public boolean isFirstLoading() {
        return isFirstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        isFirstLoading = firstLoading;
    }
}
