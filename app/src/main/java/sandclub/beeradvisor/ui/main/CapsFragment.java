package sandclub.beeradvisor.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.BeerRecyclerViewAdapter;
import sandclub.beeradvisor.adapter.CapsAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;

public class CapsFragment extends Fragment {

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

        new LoadDrunkBeerTask(recyclerView, layoutManager).execute();
    }

    private static class LoadDrunkBeerTask extends AsyncTask<Void, Void, List<Beer>> {

        private final RecyclerView recyclerView;
        private final RecyclerView.LayoutManager layoutManager;


        LoadDrunkBeerTask(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
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
            HashMap<Beer, String> drunkBeersMap = new HashMap<>();
            for (Beer b : beerList) {
                drunkBeersMap.put(b, b.getImage_url());
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
        }
    }
}