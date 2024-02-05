package sandclub.beeradvisor.ui.main;


import static sandclub.beeradvisor.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import java.io.ByteArrayOutputStream;
import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.ui.welcome.UserViewModel;
import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.welcome.UserViewModelFactory;
import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.util.ServiceLocator;


public class SettingsFragment extends Fragment {
    Button changePw;
    Button logout;
    Button photoUser;
    ImageView profilePhoto;
    DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    TextView nameSurname;
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        dataEncryptionUtil = new DataEncryptionUtil(getActivity().getApplication());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoUser = view.findViewById(R.id.changePhotoBtn);
        logout = view.findViewById(R.id.logoutBtn);
        nameSurname = view.findViewById(R.id.nameSurname);
        nameSurname.setMaxLines(1);  // Imposta il numero massimo di linee a 1
        nameSurname.setEllipsize(TextUtils.TruncateAt.END);
        //Listener bottone logout
        logout.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccessUser()) {
                    dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,ENCRYPTED_DATA_FILE_NAME);
                    Navigation.findNavController(view).navigate(
                            R.id.action_settingsFragment_to_welcomeActivity);
                } else {
                    Snackbar.make(view,requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        //Observer per settare nome e cognome
        userViewModel.getUserDataMutableLiveData(userViewModel.getLoggedUser().getUserId()).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccessUser()) {
                        User user = ((Result.UserResponseSuccess) result).getData();

                        profilePhoto = requireView().findViewById(R.id.profilePhoto);
                        nameSurname.setText(user.getNome() + " " + user.getCognome());
                        if(user.getPhotoUrl().equals("")){
                            Glide.with(requireContext())
                                    .load(user.getPhotoUrlGoogle())
                                    .placeholder(R.drawable.ic_app_user) // Immagine di fallback nel caso il caricamento fallisca
                                    .error(R.drawable.ic_app_user) // Immagine di fallback in caso di errore nel caricamento
                                    .circleCrop()
                                    .into(profilePhoto);
                        }else{
                            Glide.with(requireContext())
                                    .load(stringToBitmap(user.getPhotoUrl()))
                                    .error(R.drawable.ic_app_user)
                                    .circleCrop()
                                    .into(profilePhoto);
                        }
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        //Listener bottone cambioPw
        changePw = view.findViewById(R.id.changePasswordBtn);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_settings_Password);
            }
        });

        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Se hai già il permesso CAMERA, avvia l'attività della fotocamera
                    cameraActivityResultLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

                } else {
                    // Se non hai il permesso CAMERA, richiedilo all'utente
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });
    }


    //conversione immagine BitMap a string per salvarla su realtime
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String encodedString) {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    userViewModel.changePhotoMutableLiveData(userViewModel.getLoggedUser().getUserId(), bitmapToString((Bitmap) result.getData().getExtras().get("data")));
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Il permesso CAMERA è stato concesso, avvia l'attività della fotocamera
                    cameraActivityResultLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                } else {
                    // Il permesso CAMERA non è stato concesso, gestisci di conseguenza
                    Snackbar.make(requireView(), getResources().getString(R.string.camera_permission_denied), Snackbar.LENGTH_SHORT).show();
                }
            });

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

}

