package sandclub.beeradvisor.util;

import android.app.Application;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.model.BeerResponse;

public class JSONParserUtil {
    private static final String TAG = JSONParserUtil.class.getSimpleName();


    private final Application application;

    //passiamo il riferimento a livello generale di application
    public JSONParserUtil(Application application) {
        this.application = application;
    }



    public BeerResponse parseJSONFileWithGSon(String fileName) throws IOException {
        BeerResponse response = new BeerResponse();
        InputStream inputStream = application.getAssets().open(fileName);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Type listType = new TypeToken<List<Beer>>() {}.getType();
        List<Beer> beerList = new Gson().fromJson(bufferedReader, listType);

        response.setBeerList(beerList);
        return response;

    }
}
