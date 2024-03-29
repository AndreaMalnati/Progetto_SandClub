package sandclub.beeradvisor.data.service;

import static sandclub.beeradvisor.util.Constants.BEER_ENDPOINT;
import static sandclub.beeradvisor.util.Constants.BEER_PAGE_AMOUNT;
import static sandclub.beeradvisor.util.Constants.BEER_PAGE_PARAMETER;
import static sandclub.beeradvisor.util.Constants.SINGLE_BEER_PARAMETER;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;

public interface BeerApiService {



    @GET(BEER_ENDPOINT)
    Call<BeerResponse> getBeerById(
        @Query(SINGLE_BEER_PARAMETER) int id
    );

    @GET(BEER_ENDPOINT)
    Call<List<Beer>> get25Beers();

    @GET(BEER_ENDPOINT)
    Call<BeerApiResponse> getAllBeer(
      @Query(BEER_PAGE_PARAMETER) String page,
      @Query(BEER_PAGE_AMOUNT) String amount
    );
}
