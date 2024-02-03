package sandclub.beeradvisor.data.repository.beer;

import static sandclub.beeradvisor.util.Constants.BEER_API_TEST_JSON;

import android.app.Application;


import java.io.IOException;
import java.util.List;


import sandclub.beeradvisor.R;
import sandclub.beeradvisor.data.database.BeerDao;
import sandclub.beeradvisor.data.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.util.JSONParserUtil;
import sandclub.beeradvisor.util.ResponseCallback;
import sandclub.beeradvisor.util.ServiceLocator;


public class BeerMockRepository implements IBeerRepository {

    private final Application application;
    //private final ResponseCallback responseCallback;
    private final BeerDao beerDao;

    private final ResponseCallback responseCallback;



    public BeerMockRepository(Application application, ResponseCallback responseCallback) {//ResponseCallback responseCallback, ) {
        this.application = application;

        this.responseCallback = responseCallback;

        BeerRoomDatabase beerRoomDatabase = ServiceLocator.getInstance().getBeerDao(application);

        this.beerDao = beerRoomDatabase.beerDao();

    }


    @Override
    public void fetchBeer() {
        BeerResponse beerResponse = null;
        JSONParserUtil jsonParserUtil = new JSONParserUtil(application);

            try {
                beerResponse = jsonParserUtil.parseJSONFileWithGSon(BEER_API_TEST_JSON);
            } catch (IOException e) {
                e.printStackTrace();
            }


        if (beerResponse != null) {
            saveDataInDatabase(beerResponse.getBeerList());
        } else {
            responseCallback.onFailure(application.getString(R.string.error_retrieving_beers));
        }


    }

    //salva birre in database room
    private void saveDataInDatabase(List<Beer> beerList) {
        //Thread in background che non va a sovraccaricare l'app
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
