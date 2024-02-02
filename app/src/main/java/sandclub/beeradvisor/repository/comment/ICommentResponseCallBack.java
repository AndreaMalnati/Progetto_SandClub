package sandclub.beeradvisor.repository.comment;

import java.util.List;

import sandclub.beeradvisor.model.Comment;

public interface ICommentResponseCallBack {
    void onSuccessFromGettingComments(List<Comment> comments);
    void onFailureFromAddingComment(String message);
    void onFailureFromGettingComments(String message);
}
