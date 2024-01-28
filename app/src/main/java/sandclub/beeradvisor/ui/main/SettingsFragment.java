package sandclub.beeradvisor.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;
import static sandclub.beeradvisor.util.Constants.REQUEST_CAMERA_PERMISSION;
import static sandclub.beeradvisor.util.Constants.REQUEST_IMAGE_CAPTURE;
import static sandclub.beeradvisor.util.Constants.REQUEST_IMAGE_PICK;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.ServiceLocator;


public class SettingsFragment extends Fragment {
    Button changePw;
    Button logout;
    Button photoUser;
    ImageView profilePhoto;
    private UserViewModel userViewModel;
    TextView nameSurname;
    public SettingsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                    Navigation.findNavController(view).navigate(
                            R.id.action_settingsFragment_to_welcomeActivity);
                } else {
                    Snackbar.make(view,
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        //Observer per settare nome e cognome
        userViewModel.getUserDataMutableLiveData(userViewModel.getLoggedUser().getUserId()).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccessUser()) {
                        User user = ((Result.UserResponseSuccess) result).getData();
                        nameSurname.setText(user.getNome() + " " + user.getCognome());
                        //updatePhotoImageView();
                        profilePhoto = requireView().findViewById(R.id.profilePhoto);

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
                                    .error(R.drawable.ic_app_user) // Immagine di fallback in caso di errore nel caricamento
                                    .circleCrop()
                                    .into(profilePhoto);
                        }
                    } else {
                        //progressIndicator.setVisibility(View.GONE);
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

                //navController.navigate(R.id.action_settingsFragment_to_settings_Password);
            }
        });



        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Il permesso CAMERA è stato concesso, avvia l'activity della fotocamera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                // Per aprire la galleria


            } else {
                // Il permesso CAMERA non è stato concesso, gestisci di conseguenza
                Snackbar.make(requireView(), getResources().getString(R.string.camera_permission_denied), Snackbar.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // La foto è stata scattata con successo
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                // Passa l'immagine al fragment
                userViewModel.changePhotoMutableLiveData(userViewModel.getLoggedUser().getUserId(), bitmapToString(imageBitmap));
            }/*else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // L'utente ha scelto un'immagine dalla galleria
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                    userViewModel.changePhotoMutableLiveData(userViewModel.getLoggedUser().getUserId(), bitmapToString(photo));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }*/

        }

    }
}

