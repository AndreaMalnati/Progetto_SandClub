package sandclub.beeradvisor.repository;

import androidx.lifecycle.MutableLiveData;

import sandclub.beeradvisor.model.Result;

public interface IBeerRepositoryWithLiveData {

    MutableLiveData<Result> fetchAllBeer(long lastUpdate);
}
