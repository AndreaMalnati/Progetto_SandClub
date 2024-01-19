package sandclub.beeradvisor.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // La foto è stata scattata con successo
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                // Puoi fare qualcosa con l'immagine qui

            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // L'utente ha scelto un'immagine dalla galleria
                Uri selectedImageUri = data.getData();
                // Puoi fare qualcosa con l'URI dell'immagine qui
            }
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = UserViewModel.getInstance().getUser();

        TextView nameSurname = view.findViewById(R.id.nameSurname);
        nameSurname.setMaxLines(1);  // Imposta il numero massimo di linee a 1
        nameSurname.setEllipsize(TextUtils.TruncateAt.END);
        nameSurname.setText(user.getNome() + " " + user.getCognome());

        ImageView profilePhoto = view.findViewById(R.id.profilePhoto);

        // Carica l'immagine usando Glide
        Glide.with(requireContext())
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_app_user) // Immagine di fallback nel caso il caricamento fallisca
                .error(R.drawable.ic_app_user) // Immagine di fallback in caso di errore nel caricamento
                .circleCrop() // Applica la maschera circolare
                .into(profilePhoto);

        //Listener bottone cambioPw
        Button changePw = view.findViewById(R.id.changePasswordBtn);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();;
                    if (actionBar != null) { //togliere freccia indietro che esce in automatico
                        if (destination.getId() == R.id.settings_Password) {
                            // Nascondi il pulsante indietro quando sei nel fragment dei settings
                            actionBar.setDisplayHomeAsUpEnabled(false);
                        }
                    }
                });
                navController.navigate(R.id.action_settingsFragment_to_settings_Password);
            }
        });

        Button photoUser = view.findViewById(R.id.changePhotoBtn);

        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                // Per aprire la galleria
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);

            }

        });

    }
}

