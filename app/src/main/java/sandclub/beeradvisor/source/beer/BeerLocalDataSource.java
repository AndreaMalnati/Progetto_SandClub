package sandclub.beeradvisor.source.beer;

import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.UNEXPECTED_ERROR;
import static sandclub.beeradvisor.util.Constants.LAST_UPDATE;

import java.util.List;

import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.util.Constants;
import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class BeerLocalDataSource extends BaseBeerLocalDataSource {

    private final BeerDao beerDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;


    public BeerLocalDataSource(BeerRoomDatabase newsRoomDatabase,
                               SharedPreferencesUtil sharedPreferencesUtil,
                               DataEncryptionUtil dataEncryptionUtil) {
        this.beerDao = newsRoomDatabase.beerDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
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
            sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

            beerCallback.onSuccessFromLocal(beerList);
        });
    }

    @Override
    public void updateBeer(Beer beer) {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            if(beer != null) {
                int rowUpdatedCounter = beerDao.updateSingleFavoriteBeer(beer);

                // It means that the update succeeded because only one row had to be updated
                if (rowUpdatedCounter == 1) {
                    Beer updatedBeer = beerDao.getBeer(beer.getId());
                    beerCallback.onBeerFavoriteStatusChanged(updatedBeer, beerDao.getFavoriteBeer());
                } else {
                    beerCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
                }
            }else{
                List<Beer> allBeer = beerDao.getAll();
                for(Beer b : allBeer){
                    b.setSynchronized(false);
                    beerDao.updateSingleFavoriteBeer(b);
                }
            }
        });
    }

    @Override
    public void getFavoriteBeer() {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Beer> favoriteBeer = beerDao.getFavoriteBeer();
            beerCallback.onBeerFavoriteStatusChanged(favoriteBeer);
        });
    }


}
