package sandclub.beeradvisor.source;

import static sandclub.beeradvisor.util.Constants.UNEXPECTED_ERROR;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.service.BeerApiService;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class BeerLocalDataSource extends BaseBeerLocalDataSource {

    private final BeerDao beerDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public BeerLocalDataSource(BeerRoomDatabase newsRoomDatabase,
                               SharedPreferencesUtil sharedPreferencesUtil) {
        this.beerDao = newsRoomDatabase.beerDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void getBeer() {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            beerCallback.onSuccessFromLocal(beerDao.getAll());
        });
    }

    @Override
    public void insertBeer(List<Beer> beerList) {
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
            beerCallback.onSuccessFromLocal(beerList);
        });
    }


}
