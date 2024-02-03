package sandclub.beeradvisor.data.repository.user;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getUser(String nome, String cognome, String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserSignIn(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getUserData(String idToken);

    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String nome, String cognome, String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    //void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
    MutableLiveData<Result> changePassword(String token, String newPw, String oldPw);
    MutableLiveData<Result> changePhoto(String token, String imageBitmap);

    MutableLiveData<Result> addBeerPhotoDrunk(String token, int id_Beer, String image);
}
