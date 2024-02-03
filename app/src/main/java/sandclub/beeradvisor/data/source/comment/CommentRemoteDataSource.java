package sandclub.beeradvisor.data.source.comment;

import static sandclub.beeradvisor.util.Constants.COMMENT_DATABASE_REFERENCE;
import static sandclub.beeradvisor.util.Constants.DATABASE_URL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.model.Comment;

public class CommentRemoteDataSource extends BaseCommentRemoteDataSource{
    private final DatabaseReference databaseReference;

    public CommentRemoteDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = firebaseDatabase.getReference().getRef();
    }

    @Override
    public void addComment(Comment comment) {
        Log.d("cappero", "id: " + comment.getIdBeer());
        Log.d("cappero", String.valueOf(comment.getIdBeer()));
        databaseReference.child(COMMENT_DATABASE_REFERENCE).child(String.valueOf(comment.getIdBeer())).child(comment.getNameUser()).setValue(comment)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        databaseReference.child(COMMENT_DATABASE_REFERENCE).child(String.valueOf(comment.getIdBeer())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    List<Comment> listComment = new ArrayList<>();
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        listComment.add(dataSnapshot.getValue(Comment.class));

                                    }
                                    commentResponseCallBack.onSuccessFromGettingComments(listComment);
                                }else{
                                    commentResponseCallBack.onFailureFromGettingComments("No comments found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else{
                        commentResponseCallBack.onFailureFromAddingComment(task.getException().getMessage());
                    }
                });
    }

    @Override
    public void getComments(String idBeer) {
        databaseReference.child(COMMENT_DATABASE_REFERENCE).child(idBeer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<Comment> listComment = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        listComment.add(dataSnapshot.getValue(Comment.class));

                    }
                    commentResponseCallBack.onSuccessFromGettingComments(listComment);
                }else{
                    commentResponseCallBack.onFailureFromGettingComments("No comments found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
