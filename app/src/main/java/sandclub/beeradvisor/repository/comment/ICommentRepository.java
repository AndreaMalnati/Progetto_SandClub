package sandclub.beeradvisor.repository.comment;

import androidx.lifecycle.MutableLiveData;

import sandclub.beeradvisor.model.Comment;
import sandclub.beeradvisor.model.Result;

public interface ICommentRepository {
    MutableLiveData<Result> addComment(Comment comment);
    MutableLiveData<Result> getComments(String idBeer);
}
