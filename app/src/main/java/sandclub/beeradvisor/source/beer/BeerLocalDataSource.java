package sandclub.beeradvisor.source.beer;

import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import java.util.List;

import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.util.DataEncryptionUtil;
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

    @Override
    public void deleteAll() {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            int beerCounter = beerDao.getAll().size();
            int beerDelete = beerDao.deleteAll();

            //if(beerCounter == beerDelete)
                //beerCallback.onSuccessDeletion();

        });
    }


}
