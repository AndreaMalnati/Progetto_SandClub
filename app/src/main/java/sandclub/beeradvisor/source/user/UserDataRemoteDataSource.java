package sandclub.beeradvisor.source.user;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.NEW_PASSWORD_ERROR;
import static sandclub.beeradvisor.util.Constants.PASSWORD_DATABASE_REFERENCE;
import static sandclub.beeradvisor.util.Constants.PASSWORD_ERROR_GOOGLE;
import static sandclub.beeradvisor.util.Constants.USER_DATABASE_REFERENCE;
import static sandclub.beeradvisor.util.Constants.USER_LAST_DRUNK_DATABASE_REFERENCE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import sandclub.beeradvisor.R;
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



    public User returnUSerData(User user){
        return user;
    }

    @Override
    public void getUserData(String idToken) {
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Error getting data", task.getException());
                userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
            } else {
                DataSnapshot userSnapshot = task.getResult();
                User user = userSnapshot.getValue(User.class);

                // Verifica se il ramo last_drunk_beer esiste
                if (userSnapshot.hasChild("last_drunk_beer")) {
                    DataSnapshot lastDrunkBeerSnapshot = userSnapshot.child("last_drunk_beer");
                    HashMap<Integer, String> lastDrunkBeerMap = new HashMap<>();

                    // Itera sui sotto-nodi di last_drunk_beer per creare la HashMap
                    for (DataSnapshot childSnapshot : lastDrunkBeerSnapshot.getChildren()) {
                        int beerId = Integer.parseInt(childSnapshot.getKey());
                        String beerValue = childSnapshot.getValue(String.class);
                        lastDrunkBeerMap.put(beerId, beerValue);
                    }

                    // Imposta la HashMap lastDrunkBeerMap nell'oggetto User
                    user.setBirreBevute(lastDrunkBeerMap);
                }
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            }
        });
    }
    @Override
    public void changePassword(String idToken, String newPw, String oldPw){


        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String passwordDb = snapshot.child(PASSWORD_DATABASE_REFERENCE).getValue(String.class);
                    //controllo se vecchia password inserita è uguale a quella dentro database
                    if(passwordDb.equals(oldPw)){
                        if(!passwordDb.equals(newPw)){
                            databaseReference.child(USER_DATABASE_REFERENCE).
                                    child(idToken).child(PASSWORD_DATABASE_REFERENCE).setValue(newPw);
                            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                            fUser.updatePassword(newPw);
                            databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).get().addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "Error getting data", task.getException());
                                    userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                                } else {
                                    User user = task.getResult().getValue(User.class);
                                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                                }
                            });
                        }else{
                            userResponseCallback.onFailureFromRemoteDatabase(NEW_PASSWORD_ERROR);
                        }
                    }else if(passwordDb.equals("")){
                        userResponseCallback.onFailureFromRemoteDatabase(PASSWORD_ERROR_GOOGLE);
                    }else{
                        userResponseCallback.onFailureFromRemoteDatabase("old_password_error");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }


    @Override
    public void changePhoto(String idToken, String imageBitmap){
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).child("photoUrl").setValue(imageBitmap);
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


    public void addPhotoLastDrunkBeer(String token, int id_Beer, String image){
        Log.d("Testata", "Dentro");
        databaseReference.child(USER_DATABASE_REFERENCE).child(token).child(USER_LAST_DRUNK_DATABASE_REFERENCE).child(String.valueOf(id_Beer))
                .setValue(image).addOnSuccessListener(aVoid -> {
            Log.d("Testata", "Dentro successo");
            databaseReference.child(USER_DATABASE_REFERENCE).child(token).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error getting data", task.getException());
                    userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                } else {
                    Log.d("Testata", "dentro successo");
                    User user = task.getResult().getValue(User.class);
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                }
            });
        }).addOnFailureListener(e -> {
            Log.d("Testata", "Dentro failure");
            userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
        });

    }

}
