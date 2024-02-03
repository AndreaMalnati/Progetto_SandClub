package sandclub.beeradvisor.util;

import java.util.List;

import sandclub.beeradvisor.model.Beer;

public interface ResponseCallback {
    void onSuccess(List<Beer> beerList, long lastUpdate);
    void onFailure(String errorMessage);

}
