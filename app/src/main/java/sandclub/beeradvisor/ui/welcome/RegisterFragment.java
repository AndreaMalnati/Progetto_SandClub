package sandclub.beeradvisor.ui.welcome;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.USER_COLLISION_ERROR;
import static sandclub.beeradvisor.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Result;

public class RegisterFragment extends Fragment {

    TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword, editTextPassword2;
    Button btnConfirmRegister;
    FirebaseAuth mAuth;
    ProgressBar progressIndicator;
    private UserViewModel userViewModel;

    private DatabaseReference mDatabase;

    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        progressIndicator = view.findViewById(R.id.progressIndicatorRegister);
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
                if (TextUtils.isEmpty(Nome)) {
                    Snackbar.make(requireView(), getString(R.string.insertName), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Cognome)) {
                    Snackbar.make(requireView(), getString(R.string.insertSurname), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //check email corretta
                if (TextUtils.isEmpty(Email) || isValidEmail(Email) == false) {
                    Snackbar.make(requireView(), getString(R.string.emailNovalid), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    Snackbar.make(requireView(), getString(R.string.insertPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Password2)) {
                    Snackbar.make(requireView(), getString(R.string.insertRepeatPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //check se ripeti password uguale a password
                if (Password.equals(Password2) == false) {
                    Snackbar.make(requireView(), getString(R.string.noEqualPassword), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //CREAZIONE UTENTE
                if (!userViewModel.isAuthenticationError()) {
                    progressIndicator.setVisibility(View.VISIBLE);
                    userViewModel.getUserMutableLiveData(Nome, Cognome, Email, Password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccessUser()) {
                                    userViewModel.setAuthenticationError(false);
                                    Navigation.findNavController(view).navigate(
                                            R.id.action_registerFragment_to_loginFragment);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    progressIndicator.setVisibility(View.GONE);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(Nome, Cognome, Email, Password, false);
                }
            }
        });
    }
    private String getErrorMessage(String message) {
        switch(message) {
        case WEAK_PASSWORD_ERROR:
            return requireActivity().getString(R.string.error_password);
        case USER_COLLISION_ERROR:
            return requireActivity().getString(R.string.error_user_collision_message);
        default:
            return requireActivity().getString(R.string.unexpected_error);
        }
    }
    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
