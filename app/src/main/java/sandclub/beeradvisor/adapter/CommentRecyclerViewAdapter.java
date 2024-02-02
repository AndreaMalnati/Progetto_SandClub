package sandclub.beeradvisor.adapter;

import static sandclub.beeradvisor.adapter.CapsAdapter.stringToBitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.Comment;

public class CommentRecyclerViewAdapter  extends  RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>{


    private final List<Comment> commenList;


    public CommentRecyclerViewAdapter(List<Comment> commentList) {
        this.commenList = commentList;

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
        holder.bind(commenList.get(position));
    }

    @Override
    public int getItemCount() {
        if (commenList != null) {
            return commenList.size();
        }
        return 0;
    }

    public  class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView userSurname;
        private final TextView userComment;
        private final ImageView userImage;
        private final RatingBar ratingBar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.comment_card_name);
            userSurname = itemView.findViewById(R.id.comment_card_surname);
            userImage = itemView.findViewById(R.id.comment_card_image);
            userComment = itemView.findViewById(R.id.comment_card_text);
            ratingBar = itemView.findViewById(R.id.comment_card_rating);
        }

        public void bind(Comment comment) {
            String[] nameSurname = comment.getNameUser().split(" ");
            userName.setText(nameSurname[0]);
            userSurname.setText(nameSurname[1]);
            userComment.setText(comment.getComment());
            ratingBar.setRating(comment.getRating());

            String imageUrl = comment.getUserPhoto();
            if (imageUrl != null && !imageUrl.equals(".")) {
                Glide.with(itemView.getContext())
                        .load(stringToBitmap(imageUrl))
                        .into(userImage);
            }else
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_app_user)
                        .into(userImage);
        }


    }
}
