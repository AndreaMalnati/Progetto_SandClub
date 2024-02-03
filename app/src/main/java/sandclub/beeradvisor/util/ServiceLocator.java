package sandclub.beeradvisor.util;

import static sandclub.beeradvisor.util.Constants.BEER_API_BASE_URL;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ID;

import android.app.Application;


import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sandclub.beeradvisor.data.database.BeerRoomDatabase;
import sandclub.beeradvisor.data.repository.beer.BeerRepositoryWithLiveData;
import sandclub.beeradvisor.data.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.data.repository.comment.CommentRepository;
import sandclub.beeradvisor.data.repository.comment.ICommentRepository;
import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.data.repository.user.UserRepository;
import sandclub.beeradvisor.data.service.BeerApiService;
import sandclub.beeradvisor.data.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.data.source.beer.BaseBeerRemoteDataSource;
import sandclub.beeradvisor.data.source.beer.BaseFavouriteBeerDataSource;
import sandclub.beeradvisor.data.source.beer.BeerLocalDataSource;
import sandclub.beeradvisor.data.source.beer.BeerRemoteDataSource;
import sandclub.beeradvisor.data.source.beer.FavouriteBeerDataSource;
import sandclub.beeradvisor.data.source.comment.BaseCommentRemoteDataSource;
import sandclub.beeradvisor.data.source.comment.CommentRemoteDataSource;
import sandclub.beeradvisor.data.source.user.BaseUserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.data.source.user.BaseUserDataRemoteDataSource;
import sandclub.beeradvisor.data.source.user.UserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.data.source.user.UserDataRemoteDataSource;

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


    //creo il mio oggetto retrofit passandogli l'url delle api
    public BeerApiService getBeerApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BEER_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(BeerApiService.class);
    }


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

        beerLocalDataSource = new BeerLocalDataSource(getBeerDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        try {
            favoriteBeerDataSource = new FavouriteBeerDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID
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
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        BaseBeerLocalDataSource beerLocalDataSource =
                new BeerLocalDataSource(getBeerDao(application), sharedPreferencesUtil, dataEncryptionUtil);


        return new UserRepository(userRemoteAuthenticationDataSource,
                beerLocalDataSource, userDataRemoteDataSource);
    }

    public ICommentRepository getCommentRepository(Application application) {
        BaseCommentRemoteDataSource commentRemoteDataSource = new CommentRemoteDataSource();
        return new CommentRepository(commentRemoteDataSource);
    }

}
