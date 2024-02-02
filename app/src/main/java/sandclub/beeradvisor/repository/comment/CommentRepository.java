package sandclub.beeradvisor.repository.comment;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.model.Comment;
import sandclub.beeradvisor.model.CommentResponse;
import sandclub.beeradvisor.model.Result;
import sandclub.beeradvisor.source.comment.BaseCommentRemoteDataSource;

public class CommentRepository implements ICommentRepository, ICommentResponseCallBack {
    private static final String TAG = CommentRepository.class.getSimpleName();
    private BaseCommentRemoteDataSource commentRemoteDataSource;
    private final MutableLiveData<Result> commentMutableLiveData;

    public CommentRepository(BaseCommentRemoteDataSource commentRemoteDataSource) {
        this.commentRemoteDataSource = commentRemoteDataSource;
        this.commentMutableLiveData = new MutableLiveData<>();
        this.commentRemoteDataSource.setCommentResponseCallBack(this);
    }


    @Override
    public MutableLiveData<Result> addComment(Comment comment) {
        commentRemoteDataSource.addComment(comment);
        return commentMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getComments(String idBeer) {
        commentRemoteDataSource.getComments(idBeer);
        return commentMutableLiveData;
    }



    @Override
    public void onSuccessFromGettingComments(List<Comment> comments) {
        Result.CommentResponseSuccess result = new Result.CommentResponseSuccess(new CommentResponse(comments));
        commentMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromAddingComment(String message) {

    }

    @Override
    public void onFailureFromGettingComments(String message) {

    }
}
