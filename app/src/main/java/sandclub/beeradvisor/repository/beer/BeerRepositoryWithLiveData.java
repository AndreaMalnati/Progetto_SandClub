package sandclub.beeradvisor.repository.beer;

import static sandclub.beeradvisor.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.source.beer.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.source.beer.BaseFavouriteBeerDataSource;
import sandclub.beeradvisor.source.beer.BeerCallback;

public class BeerRepositoryWithLiveData implements IBeerRepositoryWithLiveData, BeerCallback {
    private static final String TAG = BeerRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allBeerMutableLiveData;
    private final MutableLiveData<Result> favoriteBeerMutableLiveData;

    private final BaseBeerRemoteDataSource beerRemoteDataSource;
private final BaseBeerLocalDataSource beerLocalDataSource;
    private final BaseFavouriteBeerDataSource backupDataSource;


    public BeerRepositoryWithLiveData(BaseBeerRemoteDataSource beerRemoteDataSource,
                                      BaseBeerLocalDataSource beerLocalDataSource,
                                      BaseFavouriteBeerDataSource favoriteBeerDataSource) {

        allBeerMutableLiveData = new MutableLiveData<>();
        favoriteBeerMutableLiveData = new MutableLiveData<>();
        this.beerRemoteDataSource = beerRemoteDataSource;
        this.beerLocalDataSource = beerLocalDataSource;
        this.backupDataSource = favoriteBeerDataSource;
        this.beerRemoteDataSource.setBeerCallback(this);
        this.beerLocalDataSource.setBeerCallback(this);
        this.backupDataSource.setBeerCallback(this);

    }

    //Metodo che scarica birre
    @Override
    public MutableLiveData<Result> fetchAllBeer(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            beerRemoteDataSource.getBeer();
        } else {
            beerLocalDataSource.getBeer();
        }
        return allBeerMutableLiveData;
    }

    @Override
    public void updateBeer(Beer beer, String idToken){
        beerLocalDataSource.updateBeer(beer);
        if(beer.isFavorite()){
            backupDataSource.addFavoriteBeer(beer, idToken);
        }else{
            backupDataSource.deleteFavoriteBeer(beer, idToken);
        }
    }

    @Override
    public void onSuccessFromRemote(BeerApiResponse beerApiResponse, long lastUpdate) {
        beerLocalDataSource.insertBeer(beerApiResponse.getBeerList());

    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allBeerMutableLiveData.postValue(result);
    }

    @Override
    public MutableLiveData<Result> getFavoriteBeer(boolean isFirstLoading, String idToken) {
        if(isFirstLoading){
            backupDataSource.getFavoriteBeer(idToken);
        }else {
            beerLocalDataSource.getFavoriteBeer(idToken);
        }
        return favoriteBeerMutableLiveData;
    }

    @Override
    public void onSuccessFromLocal(List<Beer> beerList) {
        Result.Success result = new Result.Success(new BeerResponse(beerList));
        allBeerMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allBeerMutableLiveData.postValue(resultError);
    }

    @Override
    public void onBeerFavoriteStatusChanged(Beer beer, List<Beer> favoriteBeer) {
        Result allBeerResult = allBeerMutableLiveData.getValue();

        if (allBeerResult != null && allBeerResult.isSuccess()) {
            List<Beer> oldAllBeer = ((Result.Success)allBeerResult).getData().getBeerList();
            if (oldAllBeer.contains(beer)) {
                oldAllBeer.set(oldAllBeer.indexOf(beer), beer);
                allBeerMutableLiveData.postValue(allBeerResult);
            }
        }
        favoriteBeerMutableLiveData.postValue(new Result.Success(new BeerResponse(favoriteBeer)));
    }



    @Override
    public void onBeerFavoriteStatusChanged(List<Beer> beerList, String idToken) {
        List<Beer> notSyncronizedBeerList = new ArrayList<>();
        for(Beer beer : beerList){
            if(!beer.isSynchronized()){
                notSyncronizedBeerList.add(beer);
            }
        }

        if(!notSyncronizedBeerList.isEmpty()){
            backupDataSource.synchronizeFavoriteBeer(notSyncronizedBeerList, idToken);
        }


        favoriteBeerMutableLiveData.postValue(new Result.Success(new BeerResponse(beerList)));
    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Beer> favoriteBeer) {

    }

    @Override
    public void onSuccessDeletion(){

    }

    @Override
    public void onSuccessFromCloudReading(List<Beer> beerList) {
        if(beerList != null){
            for(Beer beer : beerList){
                beer.setSynchronized(true);
            }
            beerLocalDataSource.insertBeer(beerList);
            favoriteBeerMutableLiveData.postValue(new Result.Success(new BeerResponse(beerList)));
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Beer beer, String idToken) {
        if(beer != null){
            beer.setSynchronized(false);
        }
        beerLocalDataSource.updateBeer(beer);
        backupDataSource.getFavoriteBeer(idToken);
    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

}
