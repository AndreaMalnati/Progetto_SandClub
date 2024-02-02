package sandclub.beeradvisor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;

public class BeerListAdapter extends ArrayAdapter<Beer>{
    private final List<Beer> beerList;
    private final int layout;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;

    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(Beer beer);
    }

    public BeerListAdapter(@NonNull Context context, int layout, @NonNull List<Beer> beerList,
                           OnFavoriteButtonClickListener onDeleteButtonClickListener) {
        super(context, layout, beerList);
        this.layout = layout;
        this.beerList = beerList;
        this.onFavoriteButtonClickListener = onDeleteButtonClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }
        TextView beerName;
        ImageView beerImage;
        TextView beerTagline;
        TextView beerAlcohol;
        CheckBox favoriteCheckBox;

        beerName = convertView.findViewById(R.id.beer_card_name);
        beerImage = convertView.findViewById(R.id.beer_card_image);
        beerTagline = convertView.findViewById(R.id.beer_card_tagline);
        beerAlcohol = convertView.findViewById(R.id.beer_card_alcohol);
        favoriteCheckBox = convertView.findViewById(R.id.iconFavorite2);
        String imageUrl = beerList.get(position).getImage_url();

        if (imageUrl != null && !imageUrl.equalsIgnoreCase("https://images.punkapi.com/v2/keg.png")) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(beerImage);
        }else
            Glide.with(getContext())
                    .load(R.drawable.ic_logo)
                    .into(beerImage);

        favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteButtonClickListener.onFavoriteButtonClick(beerList.get(position));
            }
        });
        if(beerList.get(position).isFavorite())
            favoriteCheckBox.setChecked(true);
        else
            favoriteCheckBox.setChecked(false);
        beerName.setText(beerList.get(position).getName());
        beerTagline.setText(beerList.get(position).getTagline());
        beerAlcohol.setText(beerList.get(position).getAbv()+"%");


        return convertView;
    }
}
