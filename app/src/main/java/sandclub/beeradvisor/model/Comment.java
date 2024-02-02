package sandclub.beeradvisor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Comment implements Parcelable {
    private int idBeer;
    private String nameUser;
    private float rating;
    private String comment;
    private String userPhoto;

    public Comment() {
    }
    public Comment( int idBeer, String nameUser, float rating, String comment, String userPhoto) {

        this.idBeer = idBeer;
        this.nameUser = nameUser;
        this.rating = rating;
        this.comment = comment;
        this.userPhoto = userPhoto;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getIdBeer() {
        return idBeer;
    }

    public void setIdBeer(int idBeer) {
        this.idBeer = idBeer;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "idBeer=" + idBeer +
                ", nameUser='" + nameUser + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return idBeer == comment1.idBeer && Float.compare(comment1.rating, rating) == 0 && Objects.equals(nameUser, comment1.nameUser) && Objects.equals(comment, comment1.comment) && Objects.equals(userPhoto, comment1.userPhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBeer, nameUser, rating, comment, userPhoto);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.idBeer);
        dest.writeString(this.nameUser);
        dest.writeFloat(this.rating);
        dest.writeString(this.comment);
        dest.writeString(this.userPhoto);
    }

    public void readFromParcel(Parcel source) {
        this.idBeer = source.readInt();
        this.nameUser = source.readString();
        this.rating = source.readFloat();
        this.comment = source.readString();
        this.userPhoto = source.readString();
    }

    protected Comment(Parcel in) {
        this.idBeer = in.readInt();
        this.nameUser = in.readString();
        this.rating = in.readFloat();
        this.comment = in.readString();
        this.userPhoto = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
