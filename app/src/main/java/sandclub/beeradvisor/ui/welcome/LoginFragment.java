package sandclub.beeradvisor.ui.welcome;




import static sandclub.beeradvisor.util.Constants.EMAIL_ADDRESS;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ID;
import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;

import static sandclub.beeradvisor.util.Constants.PASSWORD;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;
import static sandclub.beeradvisor.util.Constants.USE_NAVIGATION_COMPONENT;

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
import android.widget.ProgressBar;


import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.main.MainActivity;

import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.util.ServiceLocator;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class LoginFragment extends Fragment {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    private UserViewModel userViewModel;

    private DataEncryptionUtil dataEncryptionUtil;
    ProgressBar progressIndicator;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public LoginFragment() {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        progressIndicator = view.findViewById(R.id.progressIndicatorLogin);
        editTextEmail = view.findViewById(R.id.emailLg);
        editTextPassword = view.findViewById(R.id.passwordLg);
        buttonLogin = view.findViewById(R.id.confirmLoginBtn);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email, Password;
                Email = String.valueOf(editTextEmail.getText());
                Password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(Email) || isValidEmail(Email) == false) {
                    Snackbar.make(requireView(), getResources().getString(R.string.no_valid_email), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    Snackbar.make(requireView(), getResources().getString(R.string.no_valid_password), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Start login if email and password are ok
                if (isValidEmail(Email) & isPasswordOk(Password)) {
                    if (!userViewModel.isAuthenticationError()) {
                        progressIndicator.setVisibility(View.VISIBLE);
                        userViewModel.getUserMutableLiveData(Email, Password, true).observe(
                                getViewLifecycleOwner(), result -> {
                                    if (result.isSuccessUser()) {
                                        User user = ((Result.UserResponseSuccess) result).getData();
                                        saveLoginData(user.getUserId(), Email, Password);
                                        userViewModel.setAuthenticationError(false);
                                        retrieveUserInformationAndStartActivity(user, R.id.action_loginFragment_to_mainActivity);

                                    } else {
                                        userViewModel.setAuthenticationError(true);
                                        progressIndicator.setVisibility(View.GONE);
                                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                getErrorMessage(((Result.Error) result).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        userViewModel.getUser(Email, Password, true);
                    }
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveUserInformationAndStartActivity(User user, int destination) {


        userViewModel.getUserDataMutableLiveData(user.getUserId()).observe(
                getViewLifecycleOwner(), userDataRetrivalResul -> {

                    startActivityBasedOnCondition(MainActivity.class, destination);
                }
        );
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_login_user_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
    }
    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            editTextPassword.setError(null);
            return true;
        }
    }

    private void saveLoginData(String id,String email, String password) {
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());

        sharedPreferencesUtil.writeBooleanData(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING, true);
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID, id);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

            sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                    SHARED_PREFERENCES_FIRST_LOADING, true);

            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    id.concat(":").concat(email).concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}

