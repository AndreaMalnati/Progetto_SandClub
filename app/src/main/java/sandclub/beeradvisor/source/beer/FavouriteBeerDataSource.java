package sandclub.beeradvisor.source.beer;

import static sandclub.beeradvisor.util.Constants.DATABASE_URL;
import static sandclub.beeradvisor.util.Constants.FAVORITE_BEER_DATABASE_REFERENCE;
import static sandclub.beeradvisor.util.Constants.USER_DATABASE_REFERENCE;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import sandclub.beeradvisor.model.Beer;

public class FavouriteBeerDataSource extends BaseFavouriteBeerDataSource{
    private static final String TAG = FavouriteBeerDataSource.class.getSimpleName();
    private final DatabaseReference databaseReference;
    private final String idToken;

    public FavouriteBeerDataSource(String idToken) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }
    @Override
    public void getFavoriteBeer(String idToken){
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).
                child(FAVORITE_BEER_DATABASE_REFERENCE).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Error getting data", task.getException());
            }
            else {
                Log.d(TAG, "Successful read: " + task.getResult().getValue());

                List<Beer> beerList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Beer beer = ds.getValue(Beer.class);
                    beer.setFavorite(true);
                    beerList.add(beer);
                }
                beerCallback.onSuccessFromCloudReading(beerList);
            }
                });
    }

    @Override
    public void addFavoriteBeer(Beer beer, String idToken){
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).
                child(FAVORITE_BEER_DATABASE_REFERENCE).child(String.valueOf(beer.hashCode())).setValue(beer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        beer.setSynchronized(true);
                        beerCallback.onSuccessFromCloudWriting(beer, idToken);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        beerCallback.onFailureFromCloud(e);
                    }
                });
    }

    @Override
    public void synchronizeFavoriteBeer(List<Beer> notSynchronizedBeerList, String idToken) {
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).
                child(FAVORITE_BEER_DATABASE_REFERENCE).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<Beer> beerList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()){
                            Beer beer = ds.getValue(Beer.class);
                            beer.setSynchronized(true);
                            beerList.add(beer);
                        }
                        beerList.addAll(notSynchronizedBeerList);

                        for (Beer beer : beerList) {
                            databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).
                                    child(FAVORITE_BEER_DATABASE_REFERENCE).
                                    child(String.valueOf(beer.hashCode())).setValue(beer).addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    beer.setSynchronized(true);
                                                }
                                            }
                                    );
                        }

                    }
                });
    }

    @Override
    public void deleteFavoriteBeer(Beer beer, String idToken) {
        databaseReference.child(USER_DATABASE_REFERENCE).child(idToken).
                child(FAVORITE_BEER_DATABASE_REFERENCE).child(String.valueOf(beer.hashCode())).
                removeValue().addOnSuccessListener(aVoid ->{
                    beer.setSynchronized(false);
                    beerCallback.onSuccessFromCloudWriting(beer, idToken);
                }).addOnFailureListener(e ->{
                    beerCallback.onFailureFromCloud(e);
                });
    }

    @Override
    public void deleteAllFavoriteBeer() {

    }
}
