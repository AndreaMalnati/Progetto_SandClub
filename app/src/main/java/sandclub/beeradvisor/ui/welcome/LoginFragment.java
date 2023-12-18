package sandclub.beeradvisor.ui.welcome;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sandclub.beeradvisor.LoginActivity;
import sandclub.beeradvisor.MainActivity;
import sandclub.beeradvisor.R;

public class LoginFragment extends Fragment {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView textView;

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
            @Override
            public void onClick(View v) {
                String Email, Password;
                Email = String.valueOf(editTextEmail.getText());
                Password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(Email) || isValidEmail(Email) == false){
                    Toast.makeText(requireActivity(), "Email non valida", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(Password)){
                    Toast.makeText(requireActivity(), "Inserisci Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireActivity(), "Autenticazione Completata", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                    requireActivity().finish();


                                } else {

                                    Toast.makeText(requireActivity(), "Autenticazione Fallita",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }

    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

}