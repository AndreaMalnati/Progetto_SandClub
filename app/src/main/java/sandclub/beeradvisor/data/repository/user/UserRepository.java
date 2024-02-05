package sandclub.beeradvisor.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.data.source.beer.BaseBeerLocalDataSource;
import sandclub.beeradvisor.data.source.beer.BeerCallback;
import sandclub.beeradvisor.data.source.user.BaseUserAuthenticationRemoteDataSource;
import sandclub.beeradvisor.data.source.user.BaseUserDataRemoteDataSource;

public class UserRepository implements IUserRepository, UserResponseCallback, BeerCallback {

    private static final String TAG = UserRepository.class.getSimpleName();
    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseBeerLocalDataSource beerLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;
    private final MutableLiveData<Result> userLastBeerDrunkImageDataSourceLiveData;


    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseBeerLocalDataSource beerLocalDataSource, BaseUserDataRemoteDataSource userDataRemoteDataSource
                          ) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.beerLocalDataSource = beerLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.beerLocalDataSource.setBeerCallback(this);
        this.userLastBeerDrunkImageDataSourceLiveData = new MutableLiveData<>();
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
        userDataRemoteDataSource.getUserData(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> changePassword(String token, String newPw, String oldPw){
        userDataRemoteDataSource.changePassword(token, newPw, oldPw);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> changePhoto(String token, String imageBitmap){
        userDataRemoteDataSource.changePhoto(token, imageBitmap);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> addBeerPhotoDrunk(String token, int id_Beer, String image) {
        userDataRemoteDataSource.addPhotoLastDrunkBeer(token, id_Beer, image);
        return userMutableLiveData;
    }


    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();    }

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
    public void onSuccessDeletion() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromCloudReading(List<Beer> beerList) {

    }

    @Override
    public void onSuccessFromCloudWriting(Beer beer) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessSynchronization() {

    }

    @Override
    public void onSuccessGettingBeer(List<Beer> beerList) {

    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Beer> beerList) {

    }

    @Override
    public void onSuccessFromGettingUserPreferences() {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(
                new User("", "", "", "", ""));
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromloudWriting(HashMap<Integer, String> image) {
        userLastBeerDrunkImageDataSourceLiveData.postValue(new Result.UserResponseSuccess(
                new User("", "", "", "", "", "", "", image)));
    }

    @Override
    public void onSuccessFromRemote(BeerApiResponse beerApiResponse, long lastUpdate) {

    }

    @Override
    public void onSuccessFromRemote(BeerResponse beerResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(BeerApiResponse beerApiResponse) {

    }

    @Override
    public void onSuccessFromLocal(BeerResponse Response) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onBeerFavoriteStatusChanged(Beer beer, List<Beer> favoriteBeer) {

    }

    @Override
    public void onBeerFavoriteStatusChanged(List<Beer> beer) {

    }





}
