package sandclub.beeradvisor.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import sandclub.beeradvisor.repository.beer.BeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
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
    private RecyclerView favoriteBeersRecyclerView;
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
        IBeerRepositoryWithLiveData beerRepositoryWithLiveData =
                ServiceLocator.getInstance().getBeerRepository(
                        requireActivity().getApplication()
                );
        Log.d(TAG, "Birre caricate");


        if(beerRepositoryWithLiveData != null) {
            beerViewModel = new ViewModelProvider(
                    this,
                    new BeerViewModelFactory(beerRepositoryWithLiveData)).get(BeerViewModel.class);
        }else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
        beerList = new ArrayList<>();

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
        favoriteBeersRecyclerView = view.findViewById(R.id.recyclerViewFavoriteBeers);
        layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        favoriteBeerRecyclerViewAdapter = new NewBeerRecyclerViewAdapter(beerList,
                requireActivity().getApplication(),
                new NewBeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Snackbar.make(favoriteBeersRecyclerView, beer.getName(), Snackbar.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("beer", beer);
                        Navigation.findNavController(favoriteBeersRecyclerView).navigate(R.id.action_mainFragment_to_beerFragment, bundle);
                    }
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        Log.d("Ciaone", "dentro");
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position));
                    }
                });

        favoriteBeersRecyclerView.setLayoutManager(layoutManager);
        favoriteBeersRecyclerView.setAdapter(favoriteBeerRecyclerViewAdapter);
        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING);



        Log.d("Ciaone", "size fuori: " + beerList.size());
        Log.d("Ciaone", "isFirstLoading: " + isFirstLoading);
        beerViewModel.getFavoriteBeerLiveData(isFirstLoading).observe(getViewLifecycleOwner(),
                result -> {
                    if(result != null) {
                        if (result.isSuccess()) {
                            int initialSize = this.beerList.size();
                            Log.d("Ciaone", "size: " + initialSize);
                            this.beerList.clear();
                            Log.d("Ciaone", "size: " + this.beerList.size());
                            this.beerList.addAll(((Result.Success) result).getData().getBeerList());
                            favoriteBeerRecyclerViewAdapter.notifyDataSetChanged();

                            if (isFirstLoading) {
                                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                        Constants.SHARED_PREFERENCES_FIRST_LOADING, false);
                            }
                            favoriteBeerRecyclerViewAdapter.notifyItemRangeInserted(initialSize, this.beerList.size());
                        } else {
                            ErrorMessagesUtil errorMessagesUtil =
                                    new ErrorMessagesUtil(requireActivity().getApplication());
                            Snackbar.make(view, errorMessagesUtil.
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
