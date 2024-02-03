package sandclub.beeradvisor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeerResponse implements Parcelable {
    @SerializedName("birre")
    private List<Beer> beerList;

    public BeerResponse() {
    }

    public BeerResponse(List<Beer> beerList) {
        this.beerList = beerList;
    }

    public List<Beer> getBeerList() {
        return beerList;
    }

    public void setBeerList(List<Beer> beerList) {
        this.beerList = beerList;
    }

    @Override
    public String toString() {
        return "BeerResponse{" +
                "beerList=" + beerList +
                '}';
    }

    public static final Creator<BeerResponse> CREATOR = new Creator<BeerResponse>() {
        @Override
        public BeerResponse createFromParcel(Parcel in) {
            return new BeerResponse(in);
        }

        @Override
        public BeerResponse[] newArray(int size) {
            return new BeerResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.beerList);
    }

    public void readFromParcel(Parcel source) {
        this.beerList = source.createTypedArrayList(Beer.CREATOR);
    }

    protected BeerResponse(Parcel in) {
        this.beerList = in.createTypedArrayList(Beer.CREATOR);
    }
}
