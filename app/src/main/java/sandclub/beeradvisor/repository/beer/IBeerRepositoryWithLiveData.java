package sandclub.beeradvisor.repository.beer;

import androidx.lifecycle.MutableLiveData;

import java.util.Set;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.Result;

public interface IBeerRepositoryWithLiveData {

    MutableLiveData<Result> fetchAllBeer(long lastUpdate);
    void updateBeer(Beer beer);

    MutableLiveData<Result> getFavoriteBeer(boolean isFirstLoading);

    MutableLiveData<Result> getBeerId(Set<Integer> ids);


}
