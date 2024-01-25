package sandclub.beeradvisor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BeerApiResponse extends BeerResponse{
    private String status;
    private int totalResults;

    public BeerApiResponse() {
        super();
    }

    public BeerApiResponse(String status, int totalResults, List<Beer> birre) {
        super(birre);
        this.status = status;
        this.totalResults = totalResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return "BeerApiResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.status);
        dest.writeInt(this.totalResults);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.status = source.readString();
        this.totalResults = source.readInt();
    }

    protected BeerApiResponse(Parcel in) {
        super(in);
        this.status = in.readString();
        this.totalResults = in.readInt();
    }

    public static final Parcelable.Creator<BeerApiResponse> CREATOR = new Parcelable.Creator<BeerApiResponse>() {
        @Override
        public BeerApiResponse createFromParcel(Parcel source) {
            return new BeerApiResponse(source);
        }

        @Override
        public BeerApiResponse[] newArray(int size) {
            return new BeerApiResponse[size];
        }
    };
}
