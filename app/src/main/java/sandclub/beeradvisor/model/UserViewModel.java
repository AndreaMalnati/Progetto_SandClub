package sandclub.beeradvisor.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import sandclub.beeradvisor.repository.user.IUserRepository;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<Result> userLastBeerDrunkMutableLiveData;

    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String nome, String cognome, String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(nome, cognome, email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }
    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }
    public MutableLiveData<Result> getUserDataMutableLiveData(
            String idToken) {
        if (userMutableLiveData == null) {
            getUserData(idToken);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getGoogleUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> changePasswordMutableLiveData(String token, String newPw, String oldPw){
        if(userMutableLiveData == null){
            changePassword(token, newPw, oldPw);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> changePhotoMutableLiveData(String token, String imageBitmap){
            changePhoto(token, imageBitmap);

        return userMutableLiveData;
    }

    public MutableLiveData<Result> addPhotoBeerDrunkMutableLiveData(String token, int id_Beer, String image){
            addBeerPhotoDrunk(token, id_Beer, image);
        return userMutableLiveData;
    }

    public void addBeerPhotoDrunk(String token, int id_Beer, String image){
        userMutableLiveData = userRepository.addBeerPhotoDrunk(token, id_Beer, image);
    }

    /*public MutableLiveData<Result> getUserFavoriteNewsMutableLiveData(String idToken) {
        if (userFavoriteNewsMutableLiveData == null) {
            getUserFavoriteNews(idToken);
        }
        return userFavoriteNewsMutableLiveData;
    }*/

    /*public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }*/

    public MutableLiveData<Result> addFavouriteBeer(String idToken, Beer beer){
        if(userMutableLiveData == null){
            addBeerFavourite(idToken, beer);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getFavouriteBeer(String idToken){
        //if(userMutableLiveData == null) {
            Log.d("Ciaone", "aaa");
            getBeerFavourite(idToken);
        //}
        return userMutableLiveData;
    }



    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }


    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    /*private void getUserFavoriteNews(String idToken) {
        userFavoriteNewsMutableLiveData = userRepository.getUserFavoriteNews(idToken);
    }*/

    public void getUser(String nome, String cognome, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(nome, cognome, email, password, isUserRegistered);
    }

    public void addBeerFavourite(String idToken, Beer beer){
        userRepository.addFavouriteBeer(idToken, beer);
    }

    public void getBeerFavourite(String idToken){
        Log.d("Ciaone", "bbb");
        userRepository.getFavouriteBeer(idToken);
    }


    private void changePassword(String token, String newPw, String oldPw){
        userMutableLiveData = userRepository.changePassword(token, newPw, oldPw);
    }
    private void changePhoto(String token, String imageBitmap){
        userMutableLiveData = userRepository.changePhoto(token, imageBitmap);
    }
    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUserSignIn(email, password, isUserRegistered);
    }
    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String nome, String cognome, String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(nome, cognome, email, password, isUserRegistered);
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUserSignIn(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getUserData(token);
    }

    private void getGoogleUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }
}
