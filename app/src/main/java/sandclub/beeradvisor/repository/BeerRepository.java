package sandclub.beeradvisor.repository;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.service.BeerApiService;
import sandclub.beeradvisor.util.ServiceLocator;

public class BeerRepository implements IBeerRepository{

    private static final String TAG = BeerRepository.class.getSimpleName();

    private final Application application;
    private final BeerApiService beerApiService;
    private final BeerDao beerDao;
    private final ResponseCallback responseCallback;

    private int page = 1;

    public BeerRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.beerApiService = ServiceLocator.getInstance().getBeerApiService();
        BeerRoomDatabase beerRoomDatabase = ServiceLocator.getInstance().getBeerDao(application);

        this.beerDao = beerRoomDatabase.beerDao();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchBeer() {
        Call<List<Beer>> beerResponseCall = beerApiService.get25Beers();

        beerResponseCall.enqueue(new Callback<List<Beer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Beer>> call,
                                   @NonNull Response<List<Beer>> response) {
                if(response.body() != null && response.isSuccessful()){
                    List<Beer> beerList = response.body();
                    saveDataInDatabase(beerList);
                }else{
                    responseCallback.onFailure(application.getString(R.string.error_retrieving_beers));
                }
            }

            @Override
            public void onFailure(Call<List<Beer>> call, Throwable t) {
                responseCallback.onFailure(t.getMessage());
            }
        });
    }

    public void fetchAllBeer(){
        Call<List<Beer>> beerResponseCall = beerApiService.getAllBeer(String.valueOf(page), "80");

        beerResponseCall.enqueue(new Callback<List<Beer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Beer>> call,
                                   @NonNull Response<List<Beer>> response) {
                if(response.body() != null && response.isSuccessful()){
                    List<Beer> beerList = response.body();
                    saveDataInDatabase(beerList);
                    fetchAllBeer();
                    page++;
                }else{
                    responseCallback.onFailure(application.getString(R.string.error_retrieving_beers));
                }
            }

            @Override
            public void onFailure(Call<List<Beer>> call, Throwable t) {
                responseCallback.onFailure(t.getMessage());
            }
        });
    }

    private void saveDataInDatabase(List<Beer> beerList) {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {


            //Legge birre già presenti da database
            List<Beer> allBeer = beerDao.getAll();
            for (Beer beer : allBeer) {
                if (beerList.contains(beer)) {
                    beerList.set(beerList.indexOf(beer), beer);
                }
            }

            // Scrive le birre nel database
            beerDao.insertBeerList(beerList);
            responseCallback.onSuccess(beerList, System.currentTimeMillis());
        });
    }

}
