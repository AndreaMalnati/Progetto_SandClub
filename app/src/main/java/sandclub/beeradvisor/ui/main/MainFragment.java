package sandclub.beeradvisor.ui.main;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static sandclub.beeradvisor.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;

import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;

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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.BeerListAdapter;
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
import sandclub.beeradvisor.util.DataEncryptionUtil;
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
    private BeerListAdapter beerListAdapter;
    private List<Beer> beerList;
    private List<Beer> lastDrunkBeerList;
    private List<Beer> limitBeerList;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    RecyclerView recyclerViewNewBeer2;

    TextView seeAllNewBeers;
    TextView seeAllLastDrunk;
    public MainFragment() {

    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        beerList = new ArrayList<>();
        limitBeerList = new ArrayList<>();
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


        if(beerRepositoryWithLiveData != null) {
            beerViewModel = new ViewModelProvider(
                    this,
                    new BeerViewModelFactory(beerRepositoryWithLiveData)).get(BeerViewModel.class);
        }else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        lastDrunkBeerList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seeAllNewBeers = view.findViewById(R.id.seeAllNewBeers);
        seeAllLastDrunk = view.findViewById(R.id.seeAllLastDrunk);

        RecyclerView recyclerLastDrunk;
        RecyclerView.LayoutManager layoutManager;
        RecyclerView.LayoutManager layoutManager2;

        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        boolean first = sharedPreferencesUtil.readBooleanData(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, Constants.SHARED_PREFERENCES_FIRST_LOADING);


        recyclerLastDrunk = view.findViewById(R.id.recyclerViewNewBeer);
        layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);


        BeerRecyclerViewAdapter beerRecyclerViewAdapter = new BeerRecyclerViewAdapter(lastDrunkBeerList,
                requireActivity().getApplication(),
                new BeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("beer", beer);
                        Navigation.findNavController(recyclerLastDrunk).navigate(R.id.action_mainFragment_to_beerFragment, bundle);
                    }
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position));
                    }
                });

        recyclerLastDrunk.setLayoutManager(layoutManager);
        recyclerLastDrunk.setAdapter(beerRecyclerViewAdapter);

        recyclerViewNewBeer2 = view.findViewById(R.id.recyclerViewNewBeer2);
        layoutManager2 =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);


        NewBeerRecyclerViewAdapter beerRecyclerViewAdapter2 = new NewBeerRecyclerViewAdapter(beerList,
                requireActivity().getApplication(),
                new NewBeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Snackbar.make(recyclerViewNewBeer2, beer.getName(), Snackbar.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("beer", beer);
                        Navigation.findNavController(recyclerViewNewBeer2).navigate(R.id.action_mainFragment_to_beerFragment, bundle);
                    }
                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position));
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

                        //beerListAdapter.notifyDataSetChanged();
                        beerRecyclerViewAdapter2.notifyItemRangeInserted(initialSize, this.beerList.size());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });


        userViewModel.getUserDataMutableLiveData(userViewModel.getLoggedUser().getUserId()).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccessUser()) {
                        User user = ((Result.UserResponseSuccess) result).getData();
                        if(user.getBirreBevute() != null){
                            HashMap<Integer, String> birreBevute = user.getBirreBevute();
                            Set<Integer> keys = birreBevute.keySet();

                            beerViewModel.getBeerById(keys).observe(getViewLifecycleOwner(), resultBeer -> {
                                if(resultBeer.isSuccess()){
                                    lastDrunkBeerList.clear();
                                    lastDrunkBeerList.addAll(((Result.Success) resultBeer).getData().getBeerList());
                                   beerRecyclerViewAdapter.notifyDataSetChanged();
                                }else{
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) resultBeer).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }//TODO::Fare else con textview che compare dicendo che non ci sono birre bevute
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        /*

        String lastUpdate = "0";
        if(sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }*/

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

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
}