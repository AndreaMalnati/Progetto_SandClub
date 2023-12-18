package sandclub.beeradvisor.ui.welcome;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;

import sandclub.beeradvisor.ui.main.MainActivity;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.RegisterActivity;
import sandclub.beeradvisor.model.User;

public class AuthenticationFragment extends Fragment {

    Button login, register;

    Button google;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private Task<Void> mDatabase;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;

    //DA CAMBIARE E METTERE NEI CONST QUANDO SI MERGERA'
    String databaseUrl = "https://progetto-sandclub-default-rtdb.europe-west1.firebasedatabase.app/";

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
                    Toast.makeText(requireActivity(), "ApiException: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(requireActivity(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the sign-in was not successful
                Toast.makeText(requireActivity(), "Sign-in failed", Toast.LENGTH_SHORT).show();
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
                            User newUser = new User(user.getUid(), user.getDisplayName(), user.getDisplayName(), user.getEmail(), "", user.getPhotoUrl().toString());
                            mDatabase = FirebaseDatabase.getInstance(databaseUrl).getReference().child("user").child(user.getUid()).setValue(newUser);;
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getContext(), "Qualcosa è andato storto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}