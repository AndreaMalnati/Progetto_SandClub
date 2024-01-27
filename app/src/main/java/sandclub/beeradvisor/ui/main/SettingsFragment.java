package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;


public class SettingsFragment extends Fragment {
    private boolean isCameraOpen = false;


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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();

        TextView nameSurname = view.findViewById(R.id.nameSurname);
        nameSurname.setMaxLines(1);  // Imposta il numero massimo di linee a 1
        nameSurname.setEllipsize(TextUtils.TruncateAt.END);
        nameSurname.setText(user.getNome() + " " + user.getCognome());

        updatePhotoImageView();


        //Listener bottone cambioPw
        Button changePw = view.findViewById(R.id.changePasswordBtn);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
                    ;
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
                // Verifica se il permesso CAMERA è già concesso
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Se il permesso non è stato concesso, richiedilo
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    isCameraOpen = true;
                    optionMenu();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Il permesso CAMERA è stato concesso, avvia l'activity della fotocamera
                startCameraActivity();
                // Per aprire la galleria


            } else {
                // Il permesso CAMERA non è stato concesso, gestisci di conseguenza
                Snackbar.make(requireView(), getResources().getString(R.string.camera_permission_denied), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void startCameraActivity() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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


    //menu per scegliere tra fotocamera e galleria
    public void optionMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Scegli un'opzione");
        String[] options = {"Fotocamera", "Galleria"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // L'utente ha scelto la fotocamera
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Se il permesso non è stato concesso, richiedilo
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        // Il permesso è già concesso, avvia l'activity della fotocamera
                        //startCameraActivity();
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else if (which == 1) {
                    // L'utente ha scelto la galleria
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
                }
            }
        });
        // Visualizza il dialog
        builder.show();
    }

    private void updatePhotoImageView() {
        ImageView profilePhoto = requireView().findViewById(R.id.profilePhoto);
        User user = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();

        if (!user.getPhotoUrl().equals("")) {
            Log.d("Immagine", "dentro1");

            Glide.with(requireContext())
                    .load(stringToBitmap(user.getPhotoUrl()))
                    .error(R.drawable.ic_app_user) // Immagine di fallback in caso di errore nel caricamento
                    .circleCrop()
                    .into(profilePhoto);
        } else if(!user.getPhotoUrlGoogle().equals("") && user.getPhotoUrl().equals("")){
            Log.d("Immagine", "dentro2");
            Log.d("Immagine", user.getPhotoUrl());
            Glide.with(requireContext())
                    .load(user.getPhotoUrlGoogle())
                    .placeholder(R.drawable.ic_app_user) // Immagine di fallback nel caso il caricamento fallisca
                    .error(R.drawable.ic_app_user) // Immagine di fallback in caso di errore nel caricamento
                    .circleCrop()
                    .into(profilePhoto);
        } else{
            Glide.with(requireContext())
                    .load(user.getPhotoUrlGoogle())
                    .placeholder(R.drawable.ic_app_user) // Immagine di fallback nel caso il caricamento fallisca
                    .circleCrop()
                    .into(profilePhoto);
        }

    }

    public void handleImageResult(Bitmap imageBitmap) {
        // Qui gestisci l'immagine Bitmap ricevuta dall'activity
        // Esegui le azioni necessarie, ad esempio aggiorna l'UI
        Log.d("Testone", "dentro");

//aggiorna user con url foto

        User updateUser = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();
        Log.d("Testone", "ID" + updateUser.getUserId());

        String photourl = bitmapToString(imageBitmap);
        updateUser.setPhotoUrl(photourl);
        //aggiorna imageview

        DatabaseReference databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("user").child(updateUser.getUserId());

        databaseReference.child("photoUrl").setValue(photourl);

        updatePhotoImageView();
    }

    public void handleImageResult(Uri selectedImageUri) {
        // Qui gestisci l'URI dell'immagine ricevuto dall'activity

        // Puoi fare qualcosa con l'URI dell'immagine qui

        try { //converto oggetto Uri in BitMap
            Bitmap photo = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
            User updateUser = new User (".", ".", ".", ".", ".", ".", ".");//UserViewModel.getInstance().getUser();

            //converto bitmap in stringa e carico su database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("user").child(updateUser.getUserId());
            String photourl = bitmapToString(photo);
            //Toast.makeText(this, photourl, Toast.LENGTH_SHORT).show();
            updateUser.setPhotoUrl(photourl);

            databaseReference.child("photoUrl").setValue(photourl);
            updatePhotoImageView();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // La foto è stata scattata con successo
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                // Passa l'immagine al fragment
                handleImageResult(imageBitmap);
            }else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // L'utente ha scelto un'immagine dalla galleria
                Uri selectedImageUri = data.getData();
                // Passa l'URI dell'immagine al fragment
                handleImageResult(selectedImageUri);
            }

        }

    }
}

