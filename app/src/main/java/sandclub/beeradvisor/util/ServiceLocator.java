package sandclub.beeradvisor.util;

import static sandclub.beeradvisor.util.Constants.BEER_API_BASE_URL;

import android.app.Application;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.repository.BeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.service.BeerApiService;
import sandclub.beeradvisor.source.BaseBeerLocalDataSource;
import sandclub.beeradvisor.source.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.source.BeerLocalDataSource;
import sandclub.beeradvisor.source.BeerRemoteDataSource;

/**
 *  Registry to provide the dependencies for the classes
 *  used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns an instance of NewsApiService class using Retrofit.
     * @return an instance of NewsApiService.
     */
    //creo il mio oggetto retrofit passandogli l'url delle api
    public BeerApiService getBeerApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BEER_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(BeerApiService.class);
    }

    /**
     * Returns an instance of NewsRoomDatabase class to manage Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of NewsRoomDatabase.
     */
    public BeerRoomDatabase getBeerDao(Application application) {
        return BeerRoomDatabase.getDatabase(application);
    }
    public IBeerRepositoryWithLiveData getBeerRepository(Application application/*, boolean debugMode*/) {
        BaseBeerRemoteDataSource beerRemoteDataSource;
        BaseBeerLocalDataSource beerLocalDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        /*if (debugMode) {
            JSONParserUtil jsonParserUtil = new JSONParserUtil(application);
            beerRemoteDataSource =
                    new BeerMockRemoteDataSource(jsonParserUtil, JSONParserUtil.JsonParserType.GSON);
        } else {*/
            beerRemoteDataSource =
                    new BeerRemoteDataSource();
        //}

        beerLocalDataSource = new BeerLocalDataSource(getBeerDao(application), sharedPreferencesUtil);

        return new BeerRepositoryWithLiveData(beerRemoteDataSource, beerLocalDataSource);
    }

}
