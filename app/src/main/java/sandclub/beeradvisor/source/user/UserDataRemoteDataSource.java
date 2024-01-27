package sandclub.beeradvisor.source.user;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.USER_DATABASE_REFERENCE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

//TODO: Fare i metodi che servono per prendere i dati da database
public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource{
    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    //Scrive nuovo user su database dopo registrazione
    @Override
    public void saveUserData(User user) {
        databaseReference.child(USER_DATABASE_REFERENCE).child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                }else{
                    databaseReference.child(USER_DATABASE_REFERENCE).child(user.getUserId()).setValue(user);
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    /*@Override
    public void getLoggedUserData(String idToken, boolean fromGoogleSignin) {
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!fromGoogleSignin) {
                         User loggedUser = new User(snapshot.getKey(), snapshot.child("nome").getValue(String.class),
                                snapshot.child("cognome").getValue(String.class),
                                snapshot.child("email").getValue(String.class),
                                snapshot.child("password").getValue(String.class),
                                snapshot.child("photoUrl").getValue(String.class),
                                "");
                    } else {
                        User loggedUser = new User(snapshot.getKey(), snapshot.child("nome").getValue(String.class),
                                snapshot.child("cognome").getValue(String.class),
                                snapshot.child("email").getValue(String.class),
                                snapshot.child("password").getValue(String.class),
                                snapshot.child("photoUrl").getValue(String.class),
                                snapshot.child("photoUrlGoogle").getValue(String.class));
                        userResponseCallback.onSuccessFromRemoteDatabase(loggedUser);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });

    }*/

    public User returnUSerData(User user){
        return user;
    }

    @Override
    public void getUserData(String idToken) {
        Log.d("Testozza", idToken);
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Error getting data", task.getException());
                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            } else {
                User user = task.getResult().getValue(User.class);
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            }
        });
    }
}
