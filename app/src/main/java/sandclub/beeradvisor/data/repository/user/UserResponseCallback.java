package sandclub.beeradvisor.data.repository.user;

import java.util.HashMap;
import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessDeletion();
    void onSuccessFromRemoteDatabase(List<Beer> beerList);
    void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);
    void onSuccessLogout();
    void onSuccessFromloudWriting(HashMap<Integer, String> image);
    void onFailureFromCloud(Exception exception);
}
