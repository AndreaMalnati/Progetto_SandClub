package sandclub.beeradvisor.service;

import static sandclub.beeradvisor.util.Constants.SINGLE_BEER_ENDPOINT;
import static sandclub.beeradvisor.util.Constants.SINGLE_BEER_PARAMETER;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sandclub.beeradvisor.model.BeerResponse;

public interface BeerApiService {

    //SCRIVERE TUTTE LE CHIAMATE AL SERVIZIO A CUI CI APPOGGIAMO punkapi


    //Get di una birra con id
    @GET(SINGLE_BEER_ENDPOINT)
    Call<BeerResponse> getBeer(
        @Query(SINGLE_BEER_PARAMETER) int id
    );
}
