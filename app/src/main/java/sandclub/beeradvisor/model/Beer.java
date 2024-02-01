package sandclub.beeradvisor.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Objects;

import sandclub.beeradvisor.database.Converters;

@Entity
@TypeConverters(Converters.class)
public class Beer implements Parcelable {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "nome")
    private String name;

    @ColumnInfo(name = "tagline")
    private String tagline;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String image_url;

    @ColumnInfo(name = "abv")
    private double abv;

    @ColumnInfo(name = "ibu")
    private double ibu;

    @ColumnInfo(name = "ebc")
    private double ebc;

    @ColumnInfo(name = "srm")
    private double srm;

    @ColumnInfo(name = "food_pairing")
    private List<String> food_pairing;

    @ColumnInfo(name = "brewers_tips")
    private String brewers_tips;
    @ColumnInfo(name = "contributed_by")
    private String contributed_by;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;
// Aggiungi costruttori, getter e setter secondo necessità


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Ignore
    public Beer(int id, String name, String tagline, String description, String image_url, double abv, double ibu, double ebc, double srm, List<String> food_pairing, String brewers_tips, String contributed_by, boolean isFavorite, boolean isSynchronized) {
        this.id = id;
        this.name = name;
        this.tagline = tagline;
        this.description = description;
        this.image_url = image_url;
        this.abv = abv;
        this.ibu = ibu;
        this.ebc = ebc;
        this.srm = srm;
        this.food_pairing = food_pairing;
        this.brewers_tips = brewers_tips;
        this.contributed_by = contributed_by;
        this.isFavorite = isFavorite;
        this.isSynchronized = isSynchronized;
    }

    public Beer(int id, String name, String tagline, String description, String image_url, double abv, double ibu, double ebc, double srm, List<String> food_pairing, String brewers_tips, String contributed_by) {
       this(id, name, tagline, description, image_url, abv, ibu, ebc, srm, food_pairing, brewers_tips, contributed_by, false, false);
    }

    @Ignore
    public Beer() {

    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public List<String> getFood_pairing() {
        return food_pairing;
    }

    public void setFood_pairing(List<String> food_pairing) {
        this.food_pairing = food_pairing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getAbv() {
        return abv;
    }

    public void setAbv(double abv) {
        this.abv = abv;
    }

    public double getIbu() {
        return ibu;
    }

    public void setIbu(double ibu) {
        this.ibu = ibu;
    }

    public double getEbc() {
        return ebc;
    }

    public void setEbc(double ebc) {
        this.ebc = ebc;
    }

    public double getSrm() {
        return srm;
    }

    public void setSrm(double srm) {
        this.srm = srm;
    }


    public String getBrewers_tips() {
        return brewers_tips;
    }

    public void setBrewers_tips(String brewers_tips) {
        this.brewers_tips = brewers_tips;
    }

    public String getContributed_by() {
        return contributed_by;
    }

    public void setContributed_by(String contributed_by) {
        this.contributed_by = contributed_by;
    }

    @Override
    public String toString() {
        return "Beer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tagline='" + tagline + '\'' +
                ", description='" + description + '\'' +
                ", image_url='" + image_url + '\'' +
                ", abv=" + abv +
                ", ibu=" + ibu +
                ", ebc=" + ebc +
                ", srm=" + srm +
                ", food_pairing=" + food_pairing +
                ", brewers_tips='" + brewers_tips + '\'' +
                ", contributed_by='" + contributed_by + '\'' +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(name, beer.name) && Objects.equals(tagline, beer.tagline) && Objects.equals(description, beer.description) && Objects.equals(image_url, beer.image_url) && Objects.equals(abv, beer.abv) && Objects.equals(ibu, beer.ibu) && Objects.equals(ebc, beer.ebc) && Objects.equals(srm, beer.srm) && Objects.equals(food_pairing, beer.food_pairing) && Objects.equals(brewers_tips, beer.brewers_tips) && Objects.equals(contributed_by, beer.contributed_by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tagline, description, image_url, abv, ibu, ebc, srm, food_pairing, brewers_tips, contributed_by);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       dest.writeInt(this.id);
       dest.writeString(this.name);
       dest.writeString(this.tagline);
       dest.writeString(this.description);
       dest.writeString(this.image_url);
       dest.writeDouble(this.abv);
       dest.writeDouble(this.ibu);
       dest.writeDouble(this.ebc);
       dest.writeDouble(this.srm);
       dest.writeStringList(this.food_pairing);
       dest.writeString(this.brewers_tips);
       dest.writeString(this.contributed_by);
       dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
         dest.writeByte(this.isSynchronized ? (byte) 1 : (byte) 0);

    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.name = source.readString();
        this.tagline = source.readString();
        this.description = source.readString();
        this.image_url = source.readString();
        this.abv = source.readDouble();
        this.ibu = source.readDouble();
        this.ebc = source.readDouble();
        this.srm = source.readDouble();
        this.food_pairing = source.createStringArrayList();
        this.brewers_tips = source.readString();
        this.contributed_by = source.readString();
        this.isFavorite = source.readByte() != 0;
        this.isSynchronized = source.readByte() != 0;
    }

    protected Beer(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.tagline = in.readString();
        this.description = in.readString();
        this.image_url = in.readString();
        this.abv = in.readDouble();
        this.ibu = in.readDouble();
        this.ebc = in.readDouble();
        this.srm = in.readDouble();
        this.food_pairing = in.createStringArrayList();
        this.brewers_tips = in.readString();
        this.contributed_by = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.isSynchronized = in.readByte() != 0;
    }


    public static final Parcelable.Creator<Beer> CREATOR = new Parcelable.Creator<Beer>() {
        @Override
        public Beer createFromParcel(Parcel source) {
            return new Beer(source);
        }

        @Override
        public Beer[] newArray(int size) {
            return new Beer[size];
        }
    };
}
