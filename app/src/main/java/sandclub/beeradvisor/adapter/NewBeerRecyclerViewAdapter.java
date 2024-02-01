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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class NewBeerRecyclerViewAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int BEER_VIEW_TYPE = 0;
    private static final int LOADING_VIEW_TYPE = 1;
    public interface OnItemClickListener {
        void onBeerItemClick(Beer beer);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Beer> beerList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public NewBeerRecyclerViewAdapter(List<Beer> beerList,Application application, OnItemClickListener onItemClickListener) {
        this.beerList = beerList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (beerList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return BEER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = null;

        if (viewType == BEER_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.beer_card2_item, parent, false);
            return new BeersViewHolder(view);
        } /*else {
        view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.news_loading_item, parent, false);
        return new LoadingNewsViewHolder(view);
    }*/
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BeersViewHolder) {
            ((BeersViewHolder) holder).bind(beerList.get(position));
        } /*else if (holder instanceof LoadingNewsViewHolder) {
            ((LoadingNewsViewHolder) holder).activate();
        }*/
    }



    @Override
    public int getItemCount() {
        if (beerList != null) {
            return beerList.size();
        }
        return 0;
    }

    public  class BeersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView beerName;
        private final TextView beerTagline;

        private final TextView beerAlcohol;
        private final ImageView beerImage;
        private CheckBox favoriteCheckBox;


        public BeersViewHolder(@NonNull View itemView) {
            super(itemView);
            beerName = itemView.findViewById(R.id.beer_card_name);
            beerImage = itemView.findViewById(R.id.beer_card_image);
            beerTagline = itemView.findViewById(R.id.beer_card_tagline);
            beerAlcohol = itemView.findViewById(R.id.beer_card_alcohol);
            itemView.setOnClickListener(this);
            favoriteCheckBox = itemView.findViewById(R.id.iconFavorite2);
            favoriteCheckBox.setOnClickListener(this);// Inizializza la CheckBox

        }

        public void bind(Beer beer) {
            beerName.setText(beer.getName());
            beerTagline.setText(beer.getTagline());
            beerAlcohol.setText(String.format("%.1f", beer.getAbv())+ "%");
            favoriteCheckBox.setChecked(beer.isFavorite());

            String imageUrl = beer.getImage_url();
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
            if(v.getId() == R.id.iconFavorite2) {
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());

            }else {
                onItemClickListener.onBeerItemClick(beerList.get(getAdapterPosition()));
            }
        }
    }
}
