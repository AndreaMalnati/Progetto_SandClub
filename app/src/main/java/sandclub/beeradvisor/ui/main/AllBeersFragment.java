package sandclub.beeradvisor.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
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
import java.util.stream.Collectors;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.NewBeerRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerViewModel;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.beer.IBeerRepositoryWithLiveData;
import sandclub.beeradvisor.ui.factory.BeerViewModelFactory;
import sandclub.beeradvisor.util.Constants;
import sandclub.beeradvisor.util.ErrorMessagesUtil;
import sandclub.beeradvisor.util.ServiceLocator;

public class AllBeersFragment extends Fragment {

    private NewBeerRecyclerViewAdapter allBeersRecyclerViewAdapter;
    private RecyclerView recyclerViewAllBeers;
    private List<Beer> beerList;
    private BeerViewModel beerViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private UserViewModel userViewModel;
    //mettimi i metodi essenziali di un frgment
    //onCreateView
    public AllBeersFragment() {
        // Required empty public constructor
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_beers, container, false);
    }

    //fammi onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewAllBeers = view.findViewById(R.id.recyclerViewAllBeers);
        layoutManager = new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        ////

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

        Log.d("AllBeerFragment", "possible null " + getViewLifecycleOwner());

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
                Log.d("Ciaone", "PopupWindow dimensions: " + popupWindow.getWidth() + ", " + popupWindow.getHeight());

// Imposta il comportamento del popup
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                //popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));

                Bitmap scaledBlurredBitmap = Bitmap.createScaledBitmap(blurredBitmap, width2, height2, false);
                popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), scaledBlurredBitmap));

                //popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0,0);

                /*
                Confirmcomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.d("puzza" , userViewModel.getLoggedUser().getNome());

                        userViewModel.getUserDataMutableLiveData(userViewModel.getLoggedUser().getUserId()).observe(getViewLifecycleOwner(), result -> {
                            if (result.isSuccessUser()) {
                                User loggedUser = ((Result.UserResponseSuccess) result).getData();
                                String fullname = loggedUser.getNome() + " " + loggedUser.getCognome();
                                String photoUSer;
                                //se ha foto personalizzata
                                if(!loggedUser.getPhotoUrl().equals("")){
                                    photoUSer = loggedUser.getPhotoUrl();
                                }else if(loggedUser.getPhotoUrl().equals("") && !loggedUser.getPhotoUrlGoogle().equals("")) { //se ha foto google
                                    photoUSer = loggedUser.getPhotoUrlGoogle();
                                }else{
                                    photoUSer = ".";
                                }
                                Comment newComment = new Comment(beer.getId(), fullname, ratingBar.getRating(), commentText.getText().toString(), photoUSer);

                                commentViewModel.addCommentMutableLiveData(newComment).observe(getViewLifecycleOwner(),
                                        result2 -> {
                                            if (result2.isSuccessComment()) {
                                                Snackbar.make(view, "Commento aggiunto!", Snackbar.LENGTH_SHORT).show();
                                                popupWindow.dismiss();
                                            } else {
                                                ErrorMessagesUtil errorMessagesUtil =
                                                        new ErrorMessagesUtil(requireActivity().getApplication());
                                                Snackbar.make(view, errorMessagesUtil.
                                                                getErrorMessage(((Result.Error) result2).getMessage()),
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                });*/
            }
        });
    }

    private Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // Metodo per applicare l'effetto di sfocatura all'immagine
    private Bitmap blurBitmap(Bitmap bitmap, Context context) {
        RenderScript rs = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(25f); // Imposta il raggio di sfocatura
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }
}
