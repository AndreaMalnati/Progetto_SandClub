package sandclub.beeradvisor.ui.welcome;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;

public class RegisterFragment extends Fragment {

    TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword, editTextPassword2;
    Button btnConfirmRegister;
    FirebaseAuth mAuth;


    private DatabaseReference mDatabase;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        editTextNome = view.findViewById(R.id.nameRg);
        editTextCognome = view.findViewById(R.id.surnameRg);
        editTextEmail = view.findViewById(R.id.emailRg);
        editTextPassword = view.findViewById(R.id.passwordRg);
        editTextPassword2 = view.findViewById(R.id.password2Rg);
        btnConfirmRegister = view.findViewById(R.id.Confirm_Registration);
        mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();

        btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Nome, Cognome, Email, Password, Password2;
                Nome = String.valueOf(editTextNome.getText());
                Cognome = String.valueOf(editTextCognome.getText());
                Email = String.valueOf(editTextEmail.getText());
                Password = String.valueOf(editTextPassword.getText());
                Password2 = String.valueOf(editTextPassword2.getText());

                //CONTROLLI campi vuoti e/o campi non corretti
                if(TextUtils.isEmpty(Nome)){
                    Snackbar.make(requireView(), getString(R.string.insertName), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Cognome)){
                    Snackbar.make(requireView(), getString(R.string.insertSurname), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //check email corretta
                if(TextUtils.isEmpty(Email) || isValidEmail(Email) == false){
                    Snackbar.make(requireView(), getString(R.string.emailNovalid), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    Snackbar.make(requireView(), getString(R.string.insertPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password2)){
                    Snackbar.make(requireView(), getString(R.string.insertRepeatPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //check se ripeti password uguale a password
                if(Password.equals(Password2) == false){
                    Snackbar.make(requireView(), getString(R.string.noEqualPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //CREAZIONE UTENTE
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(requireView(), getString(R.string.RegisterComplete),
                                            Snackbar.LENGTH_SHORT).show();

                                    writeNewUser();
                                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    String errorMessage = "Authentication fallita";
                                    if (task.getException() != null) {
                                        errorMessage += " " + task.getException().getMessage();
                                    }
                                    Snackbar.make(requireView(), getString(R.string.RegisterFailed),
                                            Snackbar.LENGTH_SHORT).show();


                                }
                            }
                        });
            }
        });
    }

    public void sendData(View view){
        writeNewUser();
    }

    public void writeNewUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            User newUser = new User(userId, editTextNome.getText().toString(),
                    editTextCognome.getText().toString(),
                    editTextEmail.getText().toString(),
                    editTextPassword.getText().toString(),
                    "", "");

            mDatabase.child("user").child(User.getUserId()).setValue(newUser);
        }
    }
    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}