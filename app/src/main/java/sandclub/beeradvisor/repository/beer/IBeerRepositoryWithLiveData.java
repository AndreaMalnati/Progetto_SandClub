package sandclub.beeradvisor.repository.beer;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.Result;

public interface IBeerRepositoryWithLiveData {

    MutableLiveData<Result> fetchAllBeer(long lastUpdate);
    void updateBeer(Beer beer, String idToken);

    MutableLiveData<Result> getFavoriteBeer(boolean isFirstLoading, String idToken);


}
