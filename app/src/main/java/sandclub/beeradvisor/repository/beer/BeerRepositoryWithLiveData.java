package sandclub.beeradvisor.repository.beer;

import static sandclub.beeradvisor.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.source.beer.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.source.beer.BeerCallback;

public class BeerRepositoryWithLiveData implements IBeerRepositoryWithLiveData, BeerCallback {
    private static final String TAG = BeerRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allBeerMutableLiveData;
private final BaseBeerRemoteDataSource beerRemoteDataSource;
private final BaseBeerLocalDataSource beerLocalDataSource;

    public BeerRepositoryWithLiveData(BaseBeerRemoteDataSource beerRemoteDataSource,
                                      BaseBeerLocalDataSource beerLocalDataSource) {

        allBeerMutableLiveData = new MutableLiveData<>();
        this.beerRemoteDataSource = beerRemoteDataSource;
        this.beerLocalDataSource = beerLocalDataSource;
        this.beerRemoteDataSource.setBeerCallback(this);
        this.beerLocalDataSource.setBeerCallback(this);
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
    public void onSuccessFromRemote(BeerApiResponse beerApiResponse, long lastUpdate) {
        beerLocalDataSource.insertBeer(beerApiResponse.getBeerList());

    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allBeerMutableLiveData.postValue(result);
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
    public void onNewsFavoriteStatusChanged(Beer beer, List<Beer> favoriteBeer) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Beer> beer) {

    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Beer> favoriteBeer) {

    }
}
