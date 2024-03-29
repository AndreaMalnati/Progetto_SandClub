package sandclub.beeradvisor.ui.welcome;

import static sandclub.beeradvisor.util.Constants.COGNOME;

import static sandclub.beeradvisor.util.Constants.EMAIL_ADDRESS;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ID;
import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;
import static sandclub.beeradvisor.util.Constants.NOME;
import static sandclub.beeradvisor.util.Constants.PASSWORD;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;
import static sandclub.beeradvisor.util.Constants.USE_NAVIGATION_COMPONENT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;

import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.snackbar.Snackbar;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.security.GeneralSecurityException;

import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.main.MainActivity;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.util.DataEncryptionUtil;

import sandclub.beeradvisor.util.ServiceLocator;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class AuthenticationFragment extends Fragment {

    private static final String TAG = AuthenticationFragment.class.getSimpleName();

    Button login, register;

    Button google;
    FirebaseAuth auth;
    FirebaseDatabase database;

    private UserViewModel userViewModel;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private DataEncryptionUtil dataEncryptionUtil;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private SharedPreferencesUtil sharedPreferencesUtil;



    public AuthenticationFragment() {

    }

    public static AuthenticationFragment newInstance() {
        AuthenticationFragment fragment = new AuthenticationFragment();
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


        userViewModel.setAuthenticationError(false);
        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)

                        .setServerClientId(getString(R.string.default_web_client_id))

                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {

                            if (authenticationResult.isSuccessUser()) {
                                User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                                saveLoginData(user.getUserId(), user.getNome(), user.getCognome(), user.getEmail(), "");
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, R.id.action_authenticationFragment_to_mainActivity);

                            } else {
                                userViewModel.setAuthenticationError(true);

                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
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

    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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


        google.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_no_google_account_found_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));
    }

    private void saveLoginData(String id,String nome, String cognome, String email, String password) {
        dataEncryptionUtil = new DataEncryptionUtil(requireContext());
        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());

        if(password.equals(""))
            password = ".";
        sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING, true);
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
                    id.concat(":").concat(email).concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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
}