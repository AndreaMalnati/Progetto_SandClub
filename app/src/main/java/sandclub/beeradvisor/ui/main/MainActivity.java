package sandclub.beeradvisor.ui.main;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import sandclub.beeradvisor.R;
import sandclub.beeradvisor.database.BeerRoomDatabase;
import sandclub.beeradvisor.model.Beer;
import sandclub.beeradvisor.repository.beer.ResponseCallback;


public class MainActivity  extends  AppCompatActivity implements ResponseCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        NavController navController;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();


        Toolbar toolbar = findViewById(R.id.toolbarTop);
        setSupportActionBar(toolbar);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment,
                R.id.capsFragment,
                R.id.favoriteBeersFragment,
                R.id.settingsFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(bottomNav, navController);

// Aggiungi il listener per gestire la freccia indietro e il pulsante delle impostazioni nella barra superiore
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (destination.getId() == R.id.settingsFragment || destination.getId() == R.id.mainFragment ||
                        destination.getId() == R.id.capsFragment || destination.getId() == R.id.favoriteBeersFragment ||
                        destination.getId() == R.id.settings_Password || destination.getId() == R.id.beerFragment
                        || destination.getId() == R.id.allBeersFragment) {

                    // Nascondi la freccia indietro quando sei nel fragment delle impostazioni
                    actionBar.setDisplayHomeAsUpEnabled(false);
                }
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
        Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}