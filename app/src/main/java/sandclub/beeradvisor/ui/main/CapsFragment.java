package sandclub.beeradvisor.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import sandclub.beeradvisor.R;

import sandclub.beeradvisor.adapter.CapsAdapter;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.ui.welcome.UserViewModel;
import sandclub.beeradvisor.data.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.data.repository.user.IUserRepository;
import sandclub.beeradvisor.ui.welcome.UserViewModelFactory;

import sandclub.beeradvisor.util.ServiceLocator;

public class CapsFragment extends Fragment {

    private UserViewModel userViewModel;
    private BeerViewModel beerViewModel;


    public CapsFragment() {
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

                                beerViewModel.getBeerById(keys).observe(getViewLifecycleOwner(), resultBeer -> {
                                    if(resultBeer.isSuccess()){
                                        List<Beer> beerList = ((Result.Success) resultBeer).getData().getBeerList();

                                        HashMap<Beer, String> drunkBeersMap = new HashMap<>();
                                        for(Beer beer : beerList){
                                            drunkBeersMap.put(beer, birreBevute.get(beer.getId()));
                                        }
                                        CapsAdapter capsAdapter = new CapsAdapter(drunkBeersMap,
                                                new CapsAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(Beer beer) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putParcelable("beer", beer);
                                                        Navigation.findNavController(recyclerView).navigate(R.id.action_capsFragment_to_beerFragment, bundle);
                                                        Snackbar.make(recyclerView, beer.getName(), Snackbar.LENGTH_SHORT).show();

                                                    }
                                                });
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(capsAdapter);

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
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
}