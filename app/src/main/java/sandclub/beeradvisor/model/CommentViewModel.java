package sandclub.beeradvisor.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import sandclub.beeradvisor.repository.comment.ICommentRepository;

public class CommentViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();
    private final ICommentRepository commentRepository;
    private MutableLiveData<Result> commentMutableLiveData;

    public CommentViewModel(ICommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public MutableLiveData<Result> addCommentMutableLiveData(Comment comment){
            addComment(comment);
        return commentMutableLiveData;
    }

    public void addComment(Comment comment){
        commentMutableLiveData = commentRepository.addComment(comment);
    }

    public MutableLiveData<Result> getCommentMutableLiveData(String idBeer){
        getComment(idBeer);
        return commentMutableLiveData;
    }

    public void getComment(String idBeer){
        commentMutableLiveData = commentRepository.getComments(idBeer);
    }


}
