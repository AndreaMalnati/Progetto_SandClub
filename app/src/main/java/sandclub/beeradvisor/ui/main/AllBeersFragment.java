package sandclub.beeradvisor.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.PopupWindow;

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
import sandclub.beeradvisor.adapter.NewBeerRecyclerViewAdapter;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
import sandclub.beeradvisor.util.ErrorMessagesUtil;
import sandclub.beeradvisor.util.ServiceLocator;
import androidx.appcompat.widget.SearchView;

public class AllBeersFragment extends Fragment {

    private NewBeerRecyclerViewAdapter allBeersRecyclerViewAdapter;
    private RecyclerView recyclerViewAllBeers;
    private List<Beer> beerList;
    private BeerViewModel beerViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private UserViewModel userViewModel;
    private SearchView searchView;
    private  String previousText;
    private List<Beer> beerListBackUp;
    public AllBeersFragment() {

    }

    public static AllBeersFragment newInstance() {
        AllBeersFragment fragment = new AllBeersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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
        beerList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_all_beers, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewAllBeers = view.findViewById(R.id.recyclerViewAllBeers);
        layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
         previousText = "";


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Aggiorna la stringa precedente con quella corrente
                // Esegui il filtro delle birre con il nuovo testo
                filterBeer(newText);
                previousText = newText;

                return true;
            }
        });


        allBeersRecyclerViewAdapter = new NewBeerRecyclerViewAdapter(beerList,
                requireActivity().getApplication(),
                new NewBeerRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onBeerItemClick(Beer beer) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("beer", beer);
                        Navigation.findNavController(recyclerViewAllBeers).navigate(R.id.action_allBeersFragment_to_beerFragment, bundle);
                    }


                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        beerList.get(position).setFavorite(!beerList.get(position).isFavorite());
                        beerViewModel.updateBeer(beerList.get(position));
                    }
                });
        recyclerViewAllBeers.setLayoutManager(layoutManager);
        recyclerViewAllBeers.setAdapter(allBeersRecyclerViewAdapter);



        beerViewModel.getBeer(System.currentTimeMillis()).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.beerList.size();
                        this.beerList.clear();
                        this.beerList.addAll(((Result.Success) result).getData().getBeerList());

                        allBeersRecyclerViewAdapter.notifyItemRangeInserted(initialSize, this.beerList.size());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        TextView textViewFilters = view.findViewById(R.id.filter_text);
        textViewFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap backgroundBitmap = getBitmapFromView(rootView);

                // Applica l'effetto di sfocatura all'immagine di sfondo
                Bitmap blurredBitmap = blurBitmap(backgroundBitmap, requireContext());

                View popupView = getLayoutInflater().inflate(R.layout.beer_filters_layout, null);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height2 = displayMetrics.heightPixels;
                int width2 = displayMetrics.widthPixels;
                PopupWindow popupWindow = new PopupWindow(popupView, width2, height2, true);

                // Crea un'istanza di PopupWindow
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                popupView.startAnimation(animation);

                // Imposta il comportamento del popup
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);


                Bitmap scaledBlurredBitmap = Bitmap.createScaledBitmap(blurredBitmap, width2, height2, false);
                popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), scaledBlurredBitmap));


                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0,0);

                Button lessBitterButton = popupView.findViewById(R.id.lessBitterButton); //LB
                Button moreBitterButton = popupView.findViewById(R.id.moreBitterButton); //MB
                Button mostAlcholicButton = popupView.findViewById(R.id.mostAlcholicButton); //MA
                Button lessAlcholicButton = popupView.findViewById(R.id.lessAlcholicButton); //LA
                Button lightestButton = popupView.findViewById(R.id.lightestButton); //LE
                Button darkestButton = popupView.findViewById(R.id.darkestButton); //ME
                int count = 0;
                View.OnClickListener buttonClickListener = new View.OnClickListener() {
                    String filter = "";

                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.lessBitterButton) {

                            filter = "LB";
                        } else if (v.getId() == R.id.moreBitterButton) {

                            filter = "MB";
                        } else if (v.getId() == R.id.mostAlcholicButton) {

                            filter = "MA";
                        } else if (v.getId() == R.id.lessAlcholicButton) {

                            filter = "LA";
                        } else if (v.getId() == R.id.lightestButton) {

                            filter = "LE";
                        } else if (v.getId() == R.id.darkestButton) {

                            filter = "ME";
                        }

                        beerViewModel.getBeerFilteredMutableLiveData(filter).observe(getViewLifecycleOwner(),
                                result -> {
                                    if (result.isSuccess()) {
                                        beerList.clear();
                                        beerList.addAll(((Result.Success) result).getData().getBeerList());
                                        allBeersRecyclerViewAdapter.notifyDataSetChanged();
                                        popupWindow.dismiss();
                                    } else {
                                        ErrorMessagesUtil errorMessagesUtil =
                                                new ErrorMessagesUtil(requireActivity().getApplication());
                                        Snackbar.make(view, errorMessagesUtil.
                                                        getErrorMessage(((Result.Error) result).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                };
                lessBitterButton.setOnClickListener(buttonClickListener);
                moreBitterButton.setOnClickListener(buttonClickListener);
                mostAlcholicButton.setOnClickListener(buttonClickListener);
                lessAlcholicButton.setOnClickListener(buttonClickListener);
                lightestButton.setOnClickListener(buttonClickListener);
                darkestButton.setOnClickListener(buttonClickListener);

            }
        });
}

    public void filterBeer(String newText){
        if(beerListBackUp == null) {
             beerListBackUp = new ArrayList<>();
             beerListBackUp.addAll(beerList);
        }
        Log.d("Stampa", "Size: " + beerListBackUp.size());
        List<Beer> filteredList = new ArrayList<>();

            // Altrimenti, filtra la lista in base al testo
            for(Beer beer : beerListBackUp){
                if(beer.getName().toLowerCase().contains(newText.toLowerCase())){
                    filteredList.add(beer);
                }
            }
                allBeersRecyclerViewAdapter.filterList(filteredList);
    }


    private Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }


    private Bitmap blurBitmap(Bitmap bitmap, Context context) {
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(25f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }
}