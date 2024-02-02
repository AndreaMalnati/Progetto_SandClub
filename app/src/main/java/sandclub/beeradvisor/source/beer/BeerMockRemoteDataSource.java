package sandclub.beeradvisor.source.beer;

import static sandclub.beeradvisor.util.Constants.API_KEY_ERROR;
import static sandclub.beeradvisor.util.Constants.BEER_API_TEST_JSON;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import sandclub.beeradvisor.model.BeerApiResponse;
import sandclub.beeradvisor.model.BeerResponse;
import sandclub.beeradvisor.util.JSONParserUtil;
import sandclub.beeradvisor.model.BeerApiResponse;

/**
 * Class to get the news from a local JSON file to simulate the Web Service response.
 */
public class BeerMockRemoteDataSource extends BaseBeerRemoteDataSource {

    private final JSONParserUtil jsonParserUtil;

    public BeerMockRemoteDataSource(JSONParserUtil jsonParserUtil) {

        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getBeer() {
        BeerResponse beerApiResponse = null;

        try {

            beerApiResponse = jsonParserUtil.parseJSONFileWithGSon(BEER_API_TEST_JSON);
            Log.d("getBeer-MockRemote-Response", "ciao" + beerApiResponse.getBeerList().size());
        } catch (IOException e) {
            Log.d("getBeer-MockRemote-Response", "eccezione" );

            e.printStackTrace();
        }


        if (beerApiResponse != null) {
            Log.d("getBeer-MockRemote-Response", "!= null" + beerApiResponse.getBeerList().size());
            beerCallback.onSuccessFromRemote(beerApiResponse, System.currentTimeMillis());
        } else {
            Log.d("getBeer-MockRemote-Response", "== null" );
            beerCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
        }
    }
}
