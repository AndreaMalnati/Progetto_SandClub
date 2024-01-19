package sandclub.beeradvisor.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    private static UserViewModel instance;
    private User user;

    private UserViewModel() {
        // Costruttore privato per impedire l'istanza diretta
    }

    public static synchronized UserViewModel getInstance() {
        if (instance == null) {
            instance = new UserViewModel();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
