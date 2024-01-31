package sandclub.beeradvisor.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class BeerRecyclerViewAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{



    public interface OnItemClickListener {
        void onBeerItemClick(Beer beer);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Beer> beerList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public BeerRecyclerViewAdapter(List<Beer> beerList, Application application,
                                   OnItemClickListener onItemClickListener) {
        this.beerList = beerList;
        this.onItemClickListener = onItemClickListener;
        this.application = application;

    }
    @Override
    public int getItemViewType(int position) {
        if (beerList.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.beer_card_item, parent, false);
            return new BeersViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BeersViewHolder) {
            ((BeersViewHolder) holder).bind(beerList.get(position));
        }
    }



    @Override
    public int getItemCount() {
        if (beerList != null) {
            return beerList.size();
        }
        return 0;
    }

    public class BeersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView beerName;
        private final ImageView beerImage;
        private CheckBox favoriteCheckBox;

        public BeersViewHolder(@NonNull View itemView) {
            super(itemView);
            beerName = itemView.findViewById(R.id.beer_card_name);
            beerImage = itemView.findViewById(R.id.beer_card_image);
            itemView.setOnClickListener(this);
            favoriteCheckBox = itemView.findViewById(R.id.iconFavorite);
            favoriteCheckBox.setOnClickListener(this);// Inizializza la CheckBox

        }

        public void bind(Beer beer) {
            beerName.setText(beer.getName());
            String imageUrl = beer.getImage_url();
            favoriteCheckBox.setChecked(beer.isFavorite());

            if (imageUrl != null && !imageUrl.equalsIgnoreCase("https://images.punkapi.com/v2/keg.png")) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .into(beerImage);
            }else
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_logo)
                        .into(beerImage);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.iconFavorite) {
                Log.d("Ciaone", "cliccato");
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());

            }else {
                onItemClickListener.onBeerItemClick(beerList.get(getAdapterPosition()));
            }
        }
    }
}
