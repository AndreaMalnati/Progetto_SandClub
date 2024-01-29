package sandclub.beeradvisor.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.NewBeerRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;

public class AllBeersFragment extends Fragment {

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


        RecyclerView recyclerViewNewBeer2 = view.findViewById(R.id.recyclerViewNewBeer2);
        RecyclerView.LayoutManager layoutManager2 =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);
        new LoadVerticalBeerTask(recyclerViewNewBeer2, layoutManager2).execute();

        super.onViewCreated(view, savedInstanceState);

    }


    private static class LoadVerticalBeerTask extends AsyncTask<Void, Void, List<Beer>> {

        private RecyclerView recyclerView;
        private RecyclerView.LayoutManager layoutManager;

        LoadVerticalBeerTask(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
            this.recyclerView = recyclerView;
            this.layoutManager = layoutManager;
        }

        @Override
        protected List<Beer> doInBackground(Void... voids) {
            BeerRoomDatabase db = BeerRoomDatabase.getDatabase(recyclerView.getContext());
            BeerDao dao = db.beerDao();
            return dao.getAll();
        }

        @Override
        protected void onPostExecute(List<Beer> beerList) {
            NewBeerRecyclerViewAdapter beerRecyclerViewAdapter = new NewBeerRecyclerViewAdapter(beerList,
                    new NewBeerRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onBeerItemClick(Beer beer) {
                            Snackbar.make(recyclerView, beer.getName(), Snackbar.LENGTH_SHORT).show();
                            Navigation.findNavController(recyclerView).navigate(R.id.action_allBeersFragment_to_beerFragment);
                        }
                    });

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(beerRecyclerViewAdapter);
        }
    }

}
