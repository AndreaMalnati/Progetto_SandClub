package sandclub.beeradvisor.ui.main;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.repository.BeerMockRepository;
import sandclub.beeradvisor.repository.BeerRepository;
import sandclub.beeradvisor.repository.ResponseCallback;


public class MainActivity  extends  AppCompatActivity implements ResponseCallback {
    ImageButton home;
    BeerRoomDatabase db;

    private List<Beer> beerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db.getDatabase(getApplicationContext());

        /*BeerMockRepository mock = new BeerMockRepository(getApplication(), this);
        mock.fetchBeer();
        */
        BeerRepository rep = new BeerRepository(getApplication(), this);

            rep.fetchAllBeer();

        home = findViewById(R.id.button1);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Cliccato", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onSuccess(List<Beer> beerList, long lastUpdate) {
        if (beerList != null) {
            //this.beerList.clear();
            //this.beerList.addAll(beerList);
            Log.d(TAG, "OK");

        }
    }

    @Override
    public void onFailure(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

    }
}