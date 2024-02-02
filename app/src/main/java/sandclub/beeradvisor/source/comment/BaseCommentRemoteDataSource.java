package sandclub.beeradvisor.source.comment;

import sandclub.beeradvisor.model.Comment;
import sandclub.beeradvisor.repository.comment.ICommentResponseCallBack;

public abstract class BaseCommentRemoteDataSource {
    protected ICommentResponseCallBack commentResponseCallBack;

    public void setCommentResponseCallBack(ICommentResponseCallBack commentResponseCallBack) {
        this.commentResponseCallBack = commentResponseCallBack;
    }

    public abstract void addComment(Comment comment);
    public abstract void getComments(String idBeer);

}
