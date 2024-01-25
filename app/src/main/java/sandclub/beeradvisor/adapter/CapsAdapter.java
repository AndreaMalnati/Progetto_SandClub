
package sandclub.beeradvisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class CapsAdapter extends RecyclerView.Adapter<CapsAdapter.CapsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Beer beer);
    }
    private final HashMap<Beer, String> drunkBeersMap;
    private final List<Beer> drunkBeersList;
    private final OnItemClickListener onItemClickListener;

    public CapsAdapter(HashMap<Beer, String> drunkBeersMap, OnItemClickListener onItemClickListener) {
        this.drunkBeersMap = drunkBeersMap;
        this.onItemClickListener = onItemClickListener;
        this.drunkBeersList = new ArrayList<>(drunkBeersMap.keySet());
    }


    @NonNull
    @Override
    public CapsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_image_item, parent, false);
        return new CapsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CapsViewHolder holder, int position) {
        holder.bind(drunkBeersList.get(position), drunkBeersMap.get(drunkBeersList.get(position)));

    }

    @Override
    public int getItemCount() {
        if(drunkBeersList == null) return 0;
        return drunkBeersList.size();
    }

    public class CapsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView beerName;
        private final ImageView beerImage;

        CapsViewHolder(View itemView) {
            super(itemView);
            beerName = itemView.findViewById(R.id.beerName);
            beerImage = itemView.findViewById(R.id.userPhotoBeer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(drunkBeersList.get(getAdapterPosition()));
        }

        public void bind(Beer beer, String image_url) {
            beerName.setText(beer.getName());
            if(image_url == null || image_url.equals(".") ) {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_logo)
                        .circleCrop()
                        .into(beerImage);
            }
            else {
                Glide.with(itemView.getContext())
                        .load(image_url)
                        .circleCrop()
                        .into(beerImage);
            }
        }
    }
}