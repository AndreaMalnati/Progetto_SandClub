package sandclub.beeradvisor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CommentResponse implements Parcelable {
    private List<Comment> commentList;

    public CommentResponse() {
    }

    public CommentResponse(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "commentList=" + commentList +
                '}';
    }
    public static final Creator<CommentResponse> CREATOR = new Creator<CommentResponse>() {
        @Override
        public CommentResponse createFromParcel(Parcel in) {
            return new CommentResponse(in);
        }

        @Override
        public CommentResponse[] newArray(int size) {
            return new CommentResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.commentList);
    }

    public void readFromParcel(Parcel source) {
        this.commentList = source.createTypedArrayList(Comment.CREATOR);
    }

    protected CommentResponse(Parcel in) {
        this.commentList = in.createTypedArrayList(Comment.CREATOR);
    }
}
