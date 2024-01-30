package sandclub.beeradvisor.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.NewBeerRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;

public class FavoriteBeersFragment extends Fragment {
    private static final String TAG = FavoriteBeersFragment.class.getSimpleName();
    private List<Beer> beerList;
    private BeerViewModel beerViewModel;

    private NewBeerRecyclerViewAdapter newBeerRecyclerViewAdapter;
    //mettimi i metodi essenziali di un frgment
    //onCreateView
    public FavoriteBeersFragment() {
        // Required empty public constructor
    }

    public static FavoriteBeersFragment newInstance() {
        FavoriteBeersFragment fragment = new FavoriteBeersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beerList = new ArrayList<>();
        //beerViewModel = new ViewModelProvider(requireActivity()).get(BeerViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_beers, container, false);
    }

    //fammi onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /*RecyclerView favouriteBeer = view.findViewById(R.id.recyclerViewFavoriteBeers);

        newBeerRecyclerViewAdapter =
                new NewBeerRecyclerViewAdapter(requireContext(), R.layout., beerList,
                beer -> {
                    beer.setFavorite(false);
                    beerViewModel.removeFavoriteBeer(beer);
                });
        RecyclerView.LayoutManager layoutManager2 =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);*/


    }




}
