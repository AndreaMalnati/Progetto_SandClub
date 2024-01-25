package sandclub.beeradvisor.source.user;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

//TODO: Fare i metodi che servono per prendere i dati da database
public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{
    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {

    }
}
