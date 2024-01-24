package sandclub.beeradvisor.ui.welcome;

import static sandclub.beeradvisor.util.Constants.COGNOME;
import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.EMAIL_ADDRESS;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ID;
import static sandclub.beeradvisor.util.Constants.NOME;
import static sandclub.beeradvisor.util.Constants.PASSWORD;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.ui.main.MainActivity;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.ui.main.MainActivity;

public class AuthenticationFragment extends Fragment {

    Button login, register;

    Button google;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private Task<Void> mDatabase;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;

    private DataEncryptionUtil dataEncryptionUtil;




    public AuthenticationFragment() {
        // Required empty public constructor
    }

    public static AuthenticationFragment newInstance() {
        AuthenticationFragment fragment = new AuthenticationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_authentication, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login = view.findViewById(R.id.loginBtn);
        register = view.findViewById(R.id.registerBtn);
        google = view.findViewById(R.id.registerGoogleBtn);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_authenticationFragment_to_loginFragment);
            }
        });

        //registrazione
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_authenticationFragment_to_registerFragment);
            }
        });

        //registrazione google


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
        //signInLauncher.launch(intent);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == getActivity().RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuth(account.getIdToken());
                } catch (ApiException e) {
                    Snackbar.make(getView(), "ApiException: " + e.getStatusCode(), Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Snackbar.make(getView(), "Exception: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the sign-in was not successful
                Snackbar.make(getView(), getResources().getString(R.string.sign_in_failed), Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String[] parts = user.getDisplayName().split(" ");
                            String nome = parts[0];  // Il primo elemento è il nome
                            String cognome = parts.length > 1 ? parts[1] : "";  // Il secondo elemento è il cognome, se presente

                            DatabaseReference userRef = FirebaseDatabase.getInstance(DATABASE_URL)
                                    .getReference("user")
                                    .child(user.getUid());

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) { //se l'utente registrato con google esiste già non creo nuovo utente
                                        User newUser = new User(user.getUid(), nome, cognome, user.getEmail(), "", snapshot.child("photoUrl").getValue(String.class), user.getPhotoUrl().toString());

                                        UserViewModel.getInstance().setUser(newUser);
                                        saveLoginData(user.getUid(), nome, cognome, user.getEmail(), "");

                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        User newUser = new User(user.getUid(), nome, cognome, user.getEmail(), "", "", user.getPhotoUrl().toString());
                                        mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("user").child(user.getUid()).setValue(newUser);
                                        saveLoginData(user.getUid(), nome, cognome, user.getEmail(), "");

                                        UserViewModel.getInstance().setUser(newUser);

                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Gestisci l'errore
                                }
                            });
                        }else{
                            Snackbar.make(getView(), getResources().getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveLoginData(String id,String nome, String cognome, String email, String password) {
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
         if(password.equals(""))
            password = ".";

        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID, id);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, NOME, nome);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, COGNOME, cognome);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);



            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    id.concat(":").concat(nome).concat(":").concat(cognome).concat(":").concat(email).concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}