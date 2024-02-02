package sandclub.beeradvisor.source.beer;

import static sandclub.beeradvisor.util.Constants.API_KEY_ERROR;
import static sandclub.beeradvisor.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.service.BeerApiService;
import sandclub.beeradvisor.source.beer.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.util.ServiceLocator;

//logica che fa recupero dei dati da remoto e notifica con callback
public class BeerRemoteDataSource extends BaseBeerRemoteDataSource {
    private int page = 1;
    private final BeerApiService beerApiService;

    public BeerRemoteDataSource() {

        this.beerApiService = ServiceLocator.getInstance().getBeerApiService();
    }
    @Override
    public void getBeer() {
        Call<BeerApiResponse> beerResponseCall = beerApiService.getAllBeer(String.valueOf(page), "80");
        Log.d("Ciaone", "ciao, " + beerResponseCall);
        beerResponseCall.enqueue(new Callback<BeerApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<BeerApiResponse> call,
                                   @NonNull Response<BeerApiResponse> response) {

                if (response.body() != null && response.isSuccessful() &&
                        !response.body().getStatus().equals("error")) {
                    beerCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                    getBeer();
                    page++;
                } else {
                    beerCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BeerApiResponse> call, @NonNull Throwable t) {
                beerCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }


}
