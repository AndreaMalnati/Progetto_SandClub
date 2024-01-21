package sandclub.beeradvisor.ui.main;

import static sandclub.beeradvisor.database.BeerRoomDatabase.getDatabase;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.BeerRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.User;
import sandclub.beeradvisor.model.UserViewModel;
import sandclub.beeradvisor.repository.BeerRepository;
import sandclub.beeradvisor.repository.ResponseCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private List<Beer> randomBeerList;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerViewNewBeer = view.findViewById(R.id.recyclerViewNewBeer);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        new LoadBeerTask(recyclerViewNewBeer, layoutManager).execute();

        super.onViewCreated(view, savedInstanceState);

        User u = UserViewModel.getInstance().getUser();
        Toast.makeText(requireActivity(), u.getNome(), Toast.LENGTH_SHORT).show();

    }

    private static class LoadBeerTask extends AsyncTask<Void, Void, List<Beer>> {

        private final RecyclerView recyclerView;
        private final RecyclerView.LayoutManager layoutManager;

        LoadBeerTask(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
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
            BeerRecyclerViewAdapter beerRecyclerViewAdapter = new BeerRecyclerViewAdapter(beerList,
                    new BeerRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onBeerItemClick(Beer beer) {
                            Toast.makeText(recyclerView.getContext(), beer.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(beerRecyclerViewAdapter);
        }
    }

}