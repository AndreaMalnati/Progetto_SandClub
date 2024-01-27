package sandclub.beeradvisor.source.user;

import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.repository.user.UserResponseCallback;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    //public abstract void getLoggedUserData(String idToken, boolean fromGoogleSignin);
    public abstract User returnUSerData(User user);
    public abstract void getUserData(String idToken);
        //public abstract void getUserFavoriteNews(String idToken);
    //public abstract void getUserPreferences(String idToken);
    //public abstract void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);

}
