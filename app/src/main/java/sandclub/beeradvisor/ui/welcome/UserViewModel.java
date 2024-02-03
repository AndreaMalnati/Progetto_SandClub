package sandclub.beeradvisor.ui.welcome;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;

public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userPreferencesMutableLiveData;

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



    public void getUser(String nome, String cognome, String email, String password, boolean isUserRegistered) {
        userRepository.getUser(nome, cognome, email, password, isUserRegistered);
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
