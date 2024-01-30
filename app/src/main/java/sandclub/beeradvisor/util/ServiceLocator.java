package sandclub.beeradvisor.util;

import static sandclub.beeradvisor.util.Constants.BEER_API_BASE_URL;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ID_TOKEN;

import android.app.Application;


import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.repository.beer.BeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.repository.user.UserRepository;
import sandclub.beeradvisor.service.BeerApiService;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.source.beer.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.source.beer.BaseFavouriteBeerDataSource;
import sandclub.beeradvisor.source.beer.BeerLocalDataSource;
import sandclub.beeradvisor.source.beer.BeerRemoteDataSource;
import sandclub.beeradvisor.source.beer.FavouriteBeerDataSource;
import sandclub.beeradvisor.source.user.BaseUserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.source.user.BaseUserDataRemoteDataSource;
import sandclub.beeradvisor.source.user.UserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.source.user.UserDataRemoteDataSource;

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
        BaseFavouriteBeerDataSource favoriteBeerDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);
        /*if (debugMode) {
            JSONParserUtil jsonParserUtil = new JSONParserUtil(application);
            beerRemoteDataSource =
                    new BeerMockRemoteDataSource(jsonParserUtil, JSONParserUtil.JsonParserType.GSON);
        } else {*/
            beerRemoteDataSource =
                    new BeerRemoteDataSource();
        //}

        beerLocalDataSource = new BeerLocalDataSource(getBeerDao(application), sharedPreferencesUtil);
        try {
            favoriteBeerDataSource = new FavouriteBeerDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN
                    )
            );
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
        return new BeerRepositoryWithLiveData(beerRemoteDataSource, beerLocalDataSource, favoriteBeerDataSource);
    }

    /**
     * Creates an instance of IUserRepository.
     * @return An instance of IUserRepository.
     */
    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);

        BaseBeerLocalDataSource beerLocalDataSource =
                new BeerLocalDataSource(getBeerDao(application), sharedPreferencesUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                beerLocalDataSource, userDataRemoteDataSource);
    }

}
