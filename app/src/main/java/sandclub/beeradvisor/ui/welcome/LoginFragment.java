package sandclub.beeradvisor.ui.welcome;

import static android.content.ContentValues.TAG;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.ui.main.MainActivity;
import sandclub.beeradvisor.R;

public class LoginFragment extends Fragment {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = view.findViewById(R.id.emailLg);
        editTextPassword = view.findViewById(R.id.passwordLg);
        buttonLogin = view.findViewById(R.id.confirmLoginBtn);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            Context context = getContext();
            @Override
            public void onClick(View v) {
                String Email, Password;
                Email = String.valueOf(editTextEmail.getText());
                Password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(Email) || isValidEmail(Email) == false) {
                    Toast.makeText(requireActivity(), "Email non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(requireActivity(), "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                    if (firebaseUser != null) {
                                        String userId = firebaseUser.getUid();
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("user/" + userId);


                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.exists()) {
                                                         String passwordDb = dataSnapshot.child("password").getValue(String.class);
                                                        Log.d("check", "passwordDb" + passwordDb);

                                                        User loggedUser = new User(dataSnapshot.getKey(),dataSnapshot.child("nome").getValue(String.class),
                                                                dataSnapshot.child("cognome").getValue(String.class),
                                                                dataSnapshot.child("email").getValue(String.class),
                                                                dataSnapshot.child("password").getValue(String.class),
                                                                dataSnapshot.child("photoUrl").getValue(String.class),
                                                                "");


                                                        UserViewModel.getInstance().setUser(loggedUser);

                                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                                        startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Gestisci l'errore
                                            }
                                        });


                                    } else {
                                        Toast.makeText(requireActivity(), "Autenticazione Fallita",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }



                        });
            }
        });
    };

    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

}

