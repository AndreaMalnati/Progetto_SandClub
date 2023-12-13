package sandclub.beeradvisor.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import sandclub.beeradvisor.database.Converters;

@Entity
@TypeConverters(Converters.class)
public class Beer {
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
    private BeerFoodPairing food_pairing;

    @ColumnInfo(name = "brewers_tips")
    private String brewers_tips;
    @ColumnInfo(name = "contributed_by")
    private String contributed_by;


// Aggiungi costruttori, getter e setter secondo necessità


    public Beer(int id, String name, String tagline, String description, String image_url, double abv, double ibu, double ebc, double srm, BeerFoodPairing food_pairing, String brewers_tips, String contributed_by) {
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
    }

    public Beer() {

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

    public static void setName(String name) {
        this.name = name;
    }

    public String getTagline() {
        return tagline;
    }

    public static void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public static void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getAbv() {
        return abv;
    }

    public static void setAbv(double abv) {
        this.abv = abv;
    }

    public double getIbu() {
        return ibu;
    }

    public static void setIbu(double ibu) {
        this.ibu = ibu;
    }

    public double getEbc() {
        return ebc;
    }

    public static void setEbc(double ebc) {
        this.ebc = ebc;
    }

    public double getSrm() {
        return srm;
    }

    public static void setSrm(double srm) {
        this.srm = srm;
    }

    public BeerFoodPairing getFood_pairing() {
        return food_pairing;
    }

    public void setFood_pairing(BeerFoodPairing food_pairing) {
        this.food_pairing = food_pairing;
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
                '}';
    }
}
