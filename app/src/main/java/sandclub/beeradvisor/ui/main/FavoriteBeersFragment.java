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
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.Constants;
import sandclub.beeradvisor.util.ErrorMessagesUtil;
import sandclub.beeradvisor.util.ServiceLocator;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

public class FavoriteBeersFragment extends Fragment {
    private static final String TAG = FavoriteBeersFragment.class.getSimpleName();
    private List<Beer> beerList;
    private BeerViewModel beerViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private UserViewModel userViewModel;
    private NewBeerRecyclerViewAdapter favoriteBeerRecyclerViewAdapter;
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
        beerViewModel = new ViewModelProvider(requireActivity()).get(BeerViewModel.class);
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
        return inflater.inflate(R.layout.fragment_favorite_beers, container, false);
    }

    //fammi onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String idToken = userViewModel.getLoggedUser().getUserId();

        RecyclerView favoriteBeer = view.findViewById(R.id.recyclerViewFavoriteBeers);
        layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);
        favoriteBeerRecyclerViewAdapter = new NewBeerRecyclerViewAdapter(beerList, getActivity().getApplication(), new NewBeerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onBeerItemClick(Beer beer) {
                Navigation.findNavController(favoriteBeer).navigate(R.id.action_mainFragment_to_beerFragment);
                Navigation.findNavController(view).navigate(R.id.action_favoriteBeersFragment_to_beerFragment);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                Beer beer = beerList.get(position);
                beer.setFavorite(false);
                beerViewModel.updateBeer(beer);
                beerList.remove(position);
                favoriteBeerRecyclerViewAdapter.notifyItemRemoved(position);
                Snackbar.make(view, "Beer removed from favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        favoriteBeer.setLayoutManager(layoutManager);
        favoriteBeer.setAdapter(favoriteBeerRecyclerViewAdapter);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING);


        beerViewModel.getFavoriteBeerLiveData(isFirstLoading).observe(getViewLifecycleOwner(), result -> {
            if (result != null){
                beerList.clear();
                beerList.addAll(((Result.Success)result).getData().getBeerList());
                favoriteBeerRecyclerViewAdapter.notifyDataSetChanged();
                if(isFirstLoading){
                    /*sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                            Constants.SHARED_PREFERENCES_FIRST_LOADING, false);*/
                }
            }else{
                ErrorMessagesUtil errorMessagesUtil =
                        new ErrorMessagesUtil(requireActivity().getApplication());
                Snackbar.make(view, errorMessagesUtil.
                                getErrorMessage(((Result.Error)result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }




}
