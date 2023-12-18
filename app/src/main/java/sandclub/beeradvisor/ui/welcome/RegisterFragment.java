package sandclub.beeradvisor.ui.welcome;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.RegisterActivity;

public class RegisterFragment extends Fragment {

    TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword, editTextPassword2;
    Button btnConfirmRegister;
    FirebaseAuth mAuth;

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
                    Toast.makeText(requireActivity(), "Inserisci nome", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Cognome)){
                    Toast.makeText(requireActivity(), "Inserisci cognome", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check email corretta
                if(TextUtils.isEmpty(Email) || android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Toast.makeText(requireActivity(), "Email non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    Toast.makeText(requireActivity(), "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password2)){
                    Toast.makeText(requireActivity(), "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check se ripeti password uguale a password
                if(Password.equals(Password2) == false){
                    Toast.makeText(requireActivity(), "Password non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                //CREAZIONE UTENTE
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireActivity(), "Registrazione Completata.",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(requireActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}