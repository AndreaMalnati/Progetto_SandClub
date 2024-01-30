package sandclub.beeradvisor.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static sandclub.beeradvisor.util.Constants.LAST_UPDATE;
import static sandclub.beeradvisor.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import sandclub.beeradvisor.adapter.BeerRecyclerViewAdapter;
import sandclub.beeradvisor.adapter.NewBeerRecyclerViewAdapter;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.Constants;
import sandclub.beeradvisor.util.ErrorMessagesUtil;
import sandclub.beeradvisor.util.ServiceLocator;
import sandclub.beeradvisor.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private List<Beer> randomBeerList;
    private BeerViewModel beerViewModel;

    private List<Beer> beerList;
    private List<Beer> favouriteBeerList;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;


    TextView seeAllNewBeers;
    TextView seeAllLastDrunk;
    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        IUserRepository userRepository =
                ServiceLocator.getInstance().getUserRepository(
                        requireActivity().getApplication()
                );
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
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
        favouriteBeerList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seeAllNewBeers = view.findViewById(R.id.seeAllNewBeers);
        seeAllLastDrunk = view.findViewById(R.id.seeAllLastDrunk);

        RecyclerView recyclerViewNewBeer;
        RecyclerView.LayoutManager layoutManager;
        RecyclerView recyclerViewNewBeer2;
        RecyclerView.LayoutManager layoutManager2;


        recyclerViewNewBeer = view.findViewById(R.id.recyclerViewNewBeer);
        layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);


        BeerRecyclerViewAdapter beerRecyclerViewAdapter = new BeerRecyclerViewAdapter(beerList,
                requireActivity().getApplication(),
                new BeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Snackbar.make(recyclerViewNewBeer, beer.getName(), Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(recyclerViewNewBeer).navigate(R.id.action_mainFragment_to_beerFragment);
                    }
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        Log.d("Ciaone", "dentro");
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position), userViewModel.getLoggedUser().getUserId());
                    }
                });

        recyclerViewNewBeer.setLayoutManager(layoutManager);
        recyclerViewNewBeer.setAdapter(beerRecyclerViewAdapter);


        recyclerViewNewBeer2 = view.findViewById(R.id.recyclerViewNewBeer2);
        layoutManager2 =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);


        NewBeerRecyclerViewAdapter beerRecyclerViewAdapter2 = new NewBeerRecyclerViewAdapter(favouriteBeerList,
                new NewBeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Snackbar.make(recyclerViewNewBeer2, beer.getName(), Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(recyclerViewNewBeer).navigate(R.id.action_mainFragment_to_beerFragment);

                    }
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position), userViewModel.getLoggedUser().getUserId());
                    }
                });

        recyclerViewNewBeer2.setLayoutManager(layoutManager2);
        recyclerViewNewBeer2.setAdapter(beerRecyclerViewAdapter2);


        beerViewModel.getBeer(System.currentTimeMillis()).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.beerList.size();
                        this.beerList.clear();
                        this.beerList.addAll(((Result.Success) result).getData().getBeerList());
                        beerRecyclerViewAdapter.notifyItemRangeInserted(initialSize, this.beerList.size());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());
        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING);

        beerViewModel.getFavoriteBeerLiveData(isFirstLoading, userViewModel.getLoggedUser().getUserId()).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    favouriteBeerList.clear();
                    favouriteBeerList.addAll(((Result.Success)result).getData().getBeerList());
                    beerRecyclerViewAdapter2.notifyDataSetChanged();
                    if(isFirstLoading){
                        sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                                Constants.SHARED_PREFERENCES_FIRST_LOADING, false);
                    }
                } else {
                    ErrorMessagesUtil errorMessagesUtil =
                            new ErrorMessagesUtil(requireActivity().getApplication());
                    Snackbar.make(view, errorMessagesUtil.
                                    getErrorMessage(((Result.Error)result).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                }
                //progressBar.setVisibility(View.GONE);
            }
        });

        String lastUpdate = "0";
        if(sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        seeAllNewBeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_allBeersFragment);
            }
        });

        seeAllLastDrunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_capsFragment);
            }
        });

    }

}