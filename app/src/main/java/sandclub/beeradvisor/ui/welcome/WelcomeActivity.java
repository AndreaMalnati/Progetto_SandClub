package sandclub.beeradvisor.ui.welcome;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.ui.main.MainActivity;
import sandclub.beeradvisor.util.DataEncryptionUtil;

public class WelcomeActivity extends AppCompatActivity {

    String password = ".";
    String photoUrl = ".";
    String photoUrlGoogle = ".";
    DataEncryptionUtil dataEncryptionUtil;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        dataEncryptionUtil = new DataEncryptionUtil(this);

        try {
            // Leggi i dati di login dal file
            String storedLoginData = dataEncryptionUtil.readSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME);

            if (storedLoginData != null && !storedLoginData.isEmpty()) {
                //Se sono presenti informazioni di login, effettua il login automatico
                performAutoLogin(storedLoginData);
                }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void performAutoLogin(String storedLoginData) {


        String[] loginInfo = storedLoginData.split(":");
        String storedId = loginInfo[0];
        String storedName = loginInfo[1];
        String storedSurname = loginInfo[2];
        String storedEmail = loginInfo[3];

        DatabaseReference databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("user/" + storedId);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Testina", dataSnapshot.child("photoUrl").getValue(String.class));
                    password = dataSnapshot.child("password").getValue(String.class);
                    photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                    photoUrlGoogle = dataSnapshot.child("photoUrlGoogle").getValue(String.class);
                }

               if( photoUrl.equals(".")&& photoUrlGoogle.equals(".")){
                   Log.d("Testone", "Nessuna delle due foto");
                   UserViewModel.getInstance().setUser(new User(storedId, storedName, storedSurname, storedEmail, password, "", ""));

               }else if(photoUrlGoogle.equals(".") && !photoUrl.equals(".")){
                   Log.d("Testone", "Solo foto personalizzata");
                   UserViewModel.getInstance().setUser(new User(storedId, storedName, storedSurname, storedEmail, password, photoUrl, ""));

               }else if(!photoUrlGoogle.equals(".") && photoUrl.equals(".")){
                   Log.d("Testone", "Solo foto google");
                   UserViewModel.getInstance().setUser(new User(storedId, storedName, storedSurname, storedEmail, "", "", photoUrlGoogle));

               }else if(!photoUrlGoogle.equals(".") && !photoUrl.equals(".")){
                   Log.d("Testone", "Entrambe le foto");
                   UserViewModel.getInstance().setUser(new User(storedId, storedName, storedSurname, storedEmail, "", photoUrl, photoUrlGoogle));
               }

               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivity(intent);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
           // Gestisci l'errore
           }
        });
    }
}
