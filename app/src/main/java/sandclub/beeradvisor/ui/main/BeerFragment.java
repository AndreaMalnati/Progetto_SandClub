package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.ui.main.SettingsFragment.bitmapToString;
import static sandclub.beeradvisor.util.Constants.REQUEST_CAMERA_PERMISSION;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.CommentRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.ServiceLocator;

public class BeerFragment extends Fragment {
    TextView nameBeer;
    TextView tagline;
    TextView alchool;
    TextView ibu;
    TextView rating;
    TextView description;
    TextView foodPairing;
    MapView mapView;
    TextView addComment;
    private UserViewModel userViewModel;
    ImageView drunkButton;


    ScrollView scrollViewDescription;

    public BeerFragment() {
        // Required empty public constructor
    }

    public static BeerFragment newInstance() {
        BeerFragment fragment = new BeerFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameBeer = view.findViewById(R.id.beer_name);
        alchool = view.findViewById(R.id.beer_grade);
        ibu = view.findViewById(R.id.beer_ibu);
        rating = view.findViewById(R.id.ratingBar_text);
        description = view.findViewById(R.id.beer_description_text);
        foodPairing = view.findViewById(R.id.beer_foodPairings_text);
        scrollViewDescription = view.findViewById(R.id.beer_description_scroll);
        mapView = view.findViewById(R.id.mapView);
        addComment = view.findViewById(R.id.add_comment_button);

        Beer beer = getArguments().getParcelable("beer");
        Log.d("BeerFragment", "Beer: " + beer.getName());


        nameBeer.setText(beer.getName());
        alchool.setText(String.valueOf(beer.getAbv()) + "%");
        ibu.setText("ibu: " + String.valueOf(beer.getIbu()));
        description.setText(beer.getDescription());
        nameBeer.setMaxLines(1);
        nameBeer.setEllipsize(TextUtils.TruncateAt.END);
        description.setScrollbarFadingEnabled(false);
        foodPairing.setText(beer.getFood_pairing().toString());

        drunkButton = view.findViewById(R.id.sign_as_drunk_button);


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Stampa", "Cliccato su add comment");
                Navigation.findNavController(v).navigate(R.id.action_beerFragment_to_commentFragment2);

            }
        });



        drunkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Se hai già il permesso CAMERA, avvia l'attività della fotocamera
                    optionMenu();
                } else {
                    // Se non hai il permesso CAMERA, richiedilo all'utente
                    optionMenu();
                }
            }
        });
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=45.509544149287436,9.211800940043304(Maga Furla)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    String image = bitmapToString((Bitmap) result.getData().getExtras().get("data"));
                    Beer beer = getArguments().getParcelable("beer");
                    Log.d("Testone", "beer: " + beer.getId() + " image: " + image);

                    //Log.d("Testone", "newPhoto: " + newPhoto.toString());
                    userViewModel.addPhotoBeerDrunkMutableLiveData(userViewModel.getLoggedUser().getUserId(), beer.getId(), image);
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Il permesso CAMERA è stato concesso, avvia l'attività della fotocamera
            cameraActivityResultLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        } else {
            // Il permesso CAMERA non è stato concesso, gestisci di conseguenza
            Snackbar.make(requireView(), getResources().getString(R.string.camera_permission_denied), Snackbar.LENGTH_SHORT).show();
        }
    });

    public void optionMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Aggiunta a birre bevute");
        String[] options = {"Scatta foto", "Non voglio la foto"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // L'utente ha scelto di scattare la foto
                    cameraActivityResultLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                } else if (which == 1) {
                    // L'utente non vuole la foto
                    Beer beer = getArguments().getParcelable("beer");

                    //Log.d("Testone", "newPhoto: " + newPhoto.toString());
                    userViewModel.addPhotoBeerDrunkMutableLiveData(userViewModel.getLoggedUser().getUserId(), beer.getId(), ".");

                }            }
        });
        builder.show();


    }
}


