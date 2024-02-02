package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.util.Constants.INVALID_CREDENTIALS_ERROR;
import static sandclub.beeradvisor.util.Constants.INVALID_USER_ERROR;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.BeerRecyclerViewAdapter;
import sandclub.beeradvisor.adapter.CapsAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
import sandclub.beeradvisor.ui.factory.UserViewModelFactory;
import sandclub.beeradvisor.util.DataEncryptionUtil;
import sandclub.beeradvisor.util.ServiceLocator;

public class CapsFragment extends Fragment {

    private UserViewModel userViewModel;
    private BeerViewModel beerViewModel;


    public CapsFragment() {
        // Required empty public constructor
    }

    public static CapsFragment newInstance(String param1, String param2) {
        CapsFragment fragment = new CapsFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_caps, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerDrunkBeers);
        int numberOfColumns = 3;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), numberOfColumns);


        userViewModel.getUserDataMutableLiveData(userViewModel.getLoggedUser().getUserId()).observe(
                getViewLifecycleOwner(), result -> {
                    if (result.isSuccessUser()) {
                        User user = ((Result.UserResponseSuccess) result).getData();
                        if(user.getBirreBevute() != null){
                            HashMap<Integer, String> birreBevute = user.getBirreBevute();
                            Set<Integer> keys = birreBevute.keySet();

                            Log.d("capsino", keys.toString());

                                beerViewModel.getBeerById(keys).observe(getViewLifecycleOwner(), resultBeer -> {
                                    if(resultBeer.isSuccess()){
                                        List<Beer> beerList = ((Result.Success) resultBeer).getData().getBeerList();

                                        Log.d("capsino", String.valueOf(beerList.size()));
                                        Log.d("capsino", beerList.toString());
                                        HashMap<Beer, String> drunkBeersMap = new HashMap<>();
                                        for(Beer beer : beerList){
                                            drunkBeersMap.put(beer, birreBevute.get(beer.getId()));
                                        }
                                        CapsAdapter capsAdapter = new CapsAdapter(drunkBeersMap,
                                                new CapsAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(Beer beer) {
                                                        Snackbar.make(recyclerView, beer.getName(), Snackbar.LENGTH_SHORT).show();
                                                        //Navigation.findNavController(recyclerView).navigate(R.id.action_mainFragment_to_beerFragment);
                                                    }
                                                });

                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(capsAdapter);

                                        //birreBevute.put(beer.getId(), beer.getImage_url());
                                    }else{
                                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                getErrorMessage(((Result.Error) resultBeer).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }

                                });


                        }//TODO::Fare else con textview che compare dicendo che non ci sono birre bevute
                    } else {
                        //progressIndicator.setVisibility(View.GONE);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
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
}