package sandclub.beeradvisor.repository.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.source.beer.BeerCallback;
import sandclub.beeradvisor.source.user.BaseUserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.source.user.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback, BeerCallback {

    private static final String TAG = UserRepository.class.getSimpleName();
    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseBeerLocalDataSource beerLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    //private final MutableLiveData<Result> userFavoriteBeerMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseBeerLocalDataSource beerLocalDataSource, BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.beerLocalDataSource = beerLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        //this.userFavoriteBeerMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.beerLocalDataSource.setBeerCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String nome, String cognome, String email, String password, boolean isUserRegistered) {
        if(isUserRegistered){
            signIn(email, password);
        }else{
            signUp(nome, cognome, email, password);
        }
        return userMutableLiveData;
    }


    @Override
    public MutableLiveData<Result> getUserSignIn(String email, String password, boolean isUserRegistered) {
        if(isUserRegistered)
            signIn(email, password);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserData(String idToken) {
        Log.d("Testozza", "Dentro getUserData di user repository" + idToken);
        userDataRemoteDataSource.getUserData(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        return null;
    }

    @Override
    public User getLoggedUser() {
        return null;
    }

    @Override
    public void signUp(String nome, String cognome, String email, String password) {
        userRemoteDataSource.signUp(nome, cognome, email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if(user != null){
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Beer> beerList) {

    }

    @Override
    public void onSuccessFromGettingUserPreferences() {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessFromRemote(BeerApiResponse beerApiResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(List<Beer> beerList) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(Beer beer, List<Beer> favoriteBeer) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Beer> beer) {

    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Beer> favoriteBeer) {

    }
}
