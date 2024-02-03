package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.util.Constants.NEW_PASSWORD_ERROR;
import static sandclub.beeradvisor.util.Constants.OLD_PASSWORD_ERROR;
import static sandclub.beeradvisor.util.Constants.PASSWORD_ERROR_GOOGLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.ServiceLocator;

public class SettingsFragmentPassword extends Fragment {
    private UserViewModel userViewModel;

    public SettingsFragmentPassword() {

    }

    public static SettingsFragmentPassword newInstance() {
        SettingsFragmentPassword fragment = new SettingsFragmentPassword();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUserRepository userRepository =
                ServiceLocator.getInstance().getUserRepository(
                        requireActivity().getApplication()
                );
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button confirmChangePw = view.findViewById(R.id.confirmChangePasswordBtn);

        TextInputEditText oldPw = view.findViewById(R.id.oldPw);
        TextInputEditText newPw = view.findViewById(R.id.newPw);
        TextInputEditText repeatPw = view.findViewById(R.id.repeatPw);


        confirmChangePw.setOnClickListener(v -> {
            if (oldPw.getText().toString().isEmpty() || newPw.getText().toString().isEmpty() || repeatPw.getText().toString().isEmpty()) {
                Snackbar.make(view, getResources().getString(R.string.empty_fields), Snackbar.LENGTH_SHORT).show();
            }else if(!newPw.getText().toString().equals(repeatPw.getText().toString())) {
                Snackbar.make(view, getResources().getString(R.string.passwords_not_match), Snackbar.LENGTH_SHORT).show();

            }else{
                userViewModel.changePasswordMutableLiveData(userViewModel.getLoggedUser().getUserId(),
                                newPw.getText().toString(), oldPw.getText().toString())
                        .observe(getViewLifecycleOwner(), result -> {
                            if (result.isSuccessUser()) {
                                Snackbar.make(view, getResources().getString(R.string.password_changed), Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(
                                        R.id.action_settingsPasswordFragment_to_settingsFragment);
                            } else {
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
            });



    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case NEW_PASSWORD_ERROR:
                return requireActivity().getString(R.string.new_password_error);
            case OLD_PASSWORD_ERROR:
                return requireActivity().getString(R.string.old_password_error);
            case PASSWORD_ERROR_GOOGLE:
                return requireActivity().getString(R.string.password_error_google);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
}