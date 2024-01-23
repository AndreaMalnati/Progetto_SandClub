package sandclub.beeradvisor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class CommentRecyclerViewAdapter  extends  RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>{

    public interface OnItemClickListener {
        void onBeerItemClick(Beer beer);
    }

    private final List<Beer> beerList;
    private final OnItemClickListener onItemClickListener;

    public CommentRecyclerViewAdapter(List<Beer> beerList, OnItemClickListener onItemClickListener) {
        this.beerList = beerList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.comment_card_item, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(beerList.get(position));
    }

    @Override
    public int getItemCount() {
        if (beerList != null) {
            return beerList.size();
        }
        return 0;
    }

    public  class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView beerName;
        private final ImageView beerImage;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            beerName = itemView.findViewById(R.id.comment_card_name);
            beerImage = itemView.findViewById(R.id.comment_card_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Beer beer) {
            beerName.setText(beer.getName());
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
