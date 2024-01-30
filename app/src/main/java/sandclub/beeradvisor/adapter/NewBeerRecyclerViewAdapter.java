package sandclub.beeradvisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class NewBeerRecyclerViewAdapter  extends  RecyclerView.Adapter<NewBeerRecyclerViewAdapter.BeersViewHolder>{

    public interface OnItemClickListener {
        void onBeerItemClick(Beer beer);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Beer> beerList;
    private final OnItemClickListener onItemClickListener;

    public NewBeerRecyclerViewAdapter(List<Beer> beerList, OnItemClickListener onItemClickListener) {
        this.beerList = beerList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.beer_card2_item, parent, false);

        return new BeersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeersViewHolder holder, int position) {
        holder.bind(beerList.get(position));
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

        public BeersViewHolder(@NonNull View itemView) {
            super(itemView);
            beerName = itemView.findViewById(R.id.beer_card_name);
            beerImage = itemView.findViewById(R.id.beer_card_image);
            beerTagline = itemView.findViewById(R.id.beer_card_tagline);
            beerAlcohol = itemView.findViewById(R.id.beer_card_alcohol);
            itemView.setOnClickListener(this);
        }

        public void bind(Beer beer) {
            beerName.setText(beer.getName());
            beerTagline.setText(beer.getTagline());
            beerAlcohol.setText(String.format("%.1f", beer.getAbv())+ "%");
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
            onItemClickListener.onBeerItemClick(beerList.get(getAdapterPosition()));
        }
    }
}
