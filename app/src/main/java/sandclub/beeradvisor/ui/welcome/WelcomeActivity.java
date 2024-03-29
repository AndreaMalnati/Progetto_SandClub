package sandclub.beeradvisor.ui.welcome;


import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;

import static sandclub.beeradvisor.util.Constants.USE_NAVIGATION_COMPONENT;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.google.android.material.snackbar.Snackbar;


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

public class WelcomeActivity extends AppCompatActivity {


    DataEncryptionUtil dataEncryptionUtil;
    UserViewModel userViewModel;
    ProgressBar progressIndicator;
    TextView textView;
    SharedPreferencesUtil sharedPreferencesUtil;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        dataEncryptionUtil = new DataEncryptionUtil(this);


        try {
            // Leggi i dati di login dal file
            String storedLoginData = dataEncryptionUtil.readSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME);
            if (storedLoginData != null && !storedLoginData.isEmpty()) {

                    String[] loginInfo = storedLoginData.split(":");
                    String storedEmail = loginInfo[1];
                    String storedPassword = loginInfo[2];
                    if(!storedPassword.equals(".")) {
                        //Se sono presenti informazioni di login, effettua il login automatico
                        performAutoLogin(storedEmail, storedPassword);
                    }else{
                        dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                    }
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }


    private void performAutoLogin(String storedEmail, String storedPassword) {
        progressIndicator = findViewById(R.id.progressBar);
        textView = findViewById(R.id.automaticLoginText);
        textView.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.VISIBLE);

        userViewModel.getUserMutableLiveData(storedEmail, storedPassword, true).observe(
                this, result -> {
                    if (result.isSuccessUser()) {
                        User user = ((Result.UserResponseSuccess) result).getData();
                        userViewModel.setAuthenticationError(false);
                        retrieveUserInformationAndStartActivity(user, R.id.action_welcomeActivity_to_mainActivity);

                    } else {
                        userViewModel.setAuthenticationError(true);
                        progressIndicator.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        Snackbar.make(this.findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return getString(R.string.error_login_user_message);
            default:
                return getString(R.string.unexpected_error);
        }
    }
    private void retrieveUserInformationAndStartActivity(User user, int destination) {

        userViewModel.getUserDataMutableLiveData(user.getUserId()).observe(
                this, userDataRetrivalResul -> {
                    startActivityBasedOnCondition(MainActivity.class, destination);
                }
        );
    }

    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(this, R.id.welcome_nav_host_fragment).navigate(destination);
        } else {
            Intent intent = new Intent(this, destinationActivity);
            startActivity(intent);
        }
        finish();
    }
}
