package sandclub.beeradvisor.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.adapter.BeerRecyclerViewAdapter;
import sandclub.beeradvisor.adapter.CommentRecyclerViewAdapter;
import sandclub.beeradvisor.database.BeerDao;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;

public class BeerFragment extends Fragment {

    public BeerFragment() {
        // Required empty public constructor
    }

    public static BeerFragment newInstance() {
        BeerFragment fragment = new BeerFragment();
        Bundle args = new Bundle();

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
        return inflater.inflate(R.layout.fragment_beer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerViewComment = view.findViewById(R.id.recyclerViewComments);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        new BeerFragment.LoadBeerTask(recyclerViewComment, layoutManager).execute();
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
            Log.d("BeerFragment", "Number of comments: " + beerList.size());
            CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(beerList,
                    new CommentRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onBeerItemClick(Beer beer) {
                            Toast.makeText(recyclerView.getContext(), beer.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(commentRecyclerViewAdapter);
        }
    }

}
