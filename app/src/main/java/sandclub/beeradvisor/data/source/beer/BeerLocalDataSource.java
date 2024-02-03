package sandclub.beeradvisor.data.source.beer;

import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.UNEXPECTED_ERROR;
import static sandclub.beeradvisor.util.Constants.LAST_UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sandclub.beeradvisor.data.database.BeerDao;
import sandclub.beeradvisor.data.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class BeerLocalDataSource extends BaseBeerLocalDataSource {

    private final BeerDao beerDao;
    private final SharedPreferencesUtil sharedPreferencesUtil;
    private final DataEncryptionUtil dataEncryptionUtil;


    public BeerLocalDataSource(BeerRoomDatabase beerRoomDatabase,
                               SharedPreferencesUtil sharedPreferencesUtil,
                               DataEncryptionUtil dataEncryptionUtil) {
        this.beerDao = beerRoomDatabase.beerDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getBeer() {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            BeerApiResponse beerApiResponse = new BeerApiResponse();
            beerApiResponse.setBeerList(beerDao.getAll());
            beerCallback.onSuccessFromLocal(beerApiResponse);
        });
    }

    @Override
    public void getFilteredBeer(String filter){
        if(filter.equals("LB")) {
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByIbuASC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }else if(filter.equals("MB")){
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByIbuDESC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }else if(filter.equals("LA")){
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByAbvASC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }else if(filter.equals("MA")){
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByAbvDESC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }else if(filter.equals("LE")){
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByEbcASC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }else if(filter.equals("ME")) {
            BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
                BeerApiResponse beerApiResponse = new BeerApiResponse();
                beerApiResponse.setBeerList(beerDao.getBeersOrderedByEbcDESC());
                beerCallback.onSuccessFromLocal(beerApiResponse);
            });
        }
        beerCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
    }

    @Override
    public void insertBeer(BeerApiResponse beerApiResponse) {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {


                    //Legge birre già presenti da database
                    List<Beer> allBeer = beerDao.getAll();
                    List<Beer> beerList = beerApiResponse.getBeerList();

                    if (beerList != null) {

                        for (Beer beer : allBeer) {
                            if (beerList.contains(beer)) {
                                beerList.set(beerList.indexOf(beer), beer);
                            }
                        }
                }

            // Scrive le birre nel database
            beerDao.insertBeerList(beerList);
            sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

            beerCallback.onSuccessFromLocal(beerApiResponse);
        });
    }


    @Override
    public void insertBeer(List<Beer> beerList) {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {

            if (beerList != null) {
                List<Beer> allBeer = beerDao.getAll();

                for (Beer beer : allBeer) {
                    if (beerList.contains(beer)) {
                        beer.setSynchronized(true);
                        beer.setFavorite(true);
                        beerList.set(beerList.indexOf(beer), beer);
                    }
                }
            }

            // Scrive le birre nel database
            beerDao.insertBeerList(beerList);
            BeerApiResponse beerApiResponse = new BeerApiResponse();
            beerApiResponse.setBeerList(beerList);
            beerCallback.onSuccessSynchronization();
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

    @Override
    public Beer getBeerId(Set<Integer> ids) {
        BeerRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Beer> beerList = new ArrayList<>();
            for(Integer id : ids) {
                Beer beer = beerDao.getBeer(id);
                beerList.add(beer);
            }

            beerCallback.onSuccessGettingBeer(beerList);
        });
        return null;
    }


}
