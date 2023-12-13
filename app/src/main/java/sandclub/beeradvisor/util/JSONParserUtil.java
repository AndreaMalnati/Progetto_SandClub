package sandclub.beeradvisor.util;

import android.app.Application;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerFoodPairing;
import sandclub.beeradvisor.model.BeerResponse;

public class JSONParserUtil {
    private final Application application;
    /*id long
    name string
    tagline string
    description string
    image_url string
    abv double
    ibu double
    ebc double
    srm double
    food_pairing List<String>
   brewers_tips string
    */
    private final String  idParameter = "id";
    private final String  nameParameter = "name";
    private final String  taglineParameter = "tagline";
    private final String  descriptionParameter = "description";
    private final String  imageurlParameter = "image_url";
    private final String  abvParameter = "abv";
    private final String  ibuParameter = "ibu";
    private final String  ebcParameter = "ebc";
    private final String  srmParameter = "srm";
    private final String  foodpairingParameter = "food_pairing";
    private final String  brewerstipsParameter = "brewers_tips";


    //passiamo il riferimento a livello generale di application
    public JSONParserUtil(Application application) {
        this.application = application;
    }

    public BeerResponse parseJSONFileWithJsonReader(String fileName) throws IOException {
        InputStream inputStream = application.getAssets().open("beersapi-test.json");
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
        List<Beer> BeerList = null;
        BeerResponse beerResponse = new BeerResponse();

        jsonReader.beginArray(); //inizia array
        BeerList = new ArrayList<>();
        while (jsonReader.hasNext()) {
            jsonReader.beginObject(); //inizia oggetto
            Beer beer = new Beer();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject(); //inizia oggetto
                String rootJSONParam = jsonReader.nextName();
                if (rootJSONParam.equals(idParameter)) {
                    beer.setId(jsonReader.nextInt());
                } else if (rootJSONParam.equals(nameParameter)) {
                    beer.setName(jsonReader.nextString());
                } else if (rootJSONParam.equals(taglineParameter)) {
                    beer.setTagline(jsonReader.nextString());
                } else if (rootJSONParam.equals(descriptionParameter)) {
                    beer.setDescription(jsonReader.nextString());
                } else if (rootJSONParam.equals(imageurlParameter)) {
                    beer.setImage_url(jsonReader.nextString());
                } else if (rootJSONParam.equals(abvParameter)) {
                    beer.setAbv(jsonReader.nextDouble());
                } else if (rootJSONParam.equals(ibuParameter)) {
                    beer.setIbu(jsonReader.nextDouble());
                } else if (rootJSONParam.equals(ebcParameter)) {
                    beer.setEbc(jsonReader.nextDouble());
                } else if (rootJSONParam.equals(srmParameter)) {
                    beer.setSrm(jsonReader.nextDouble());
                } else if (rootJSONParam.equals(foodpairingParameter)) {
                    beer.setName(jsonReader.nextString());
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        beer.setFood_pairing(new BeerFoodPairing(jsonReader.nextString()));
                    }
                    jsonReader.endArray();
                } else
                    jsonReader.skipValue();

            }

            jsonReader.endObject(); //fine oggetto
            BeerList.add(beer); //aggiunge la birra alla List
        }
        jsonReader.endArray(); //fine array
        beerResponse.setBeerList(BeerList);

        return beerResponse;
    }
}
