package sandclub.beeradvisor.database;

import static sandclub.beeradvisor.util.Constants.BEER_DATABASE_NAME;
import static sandclub.beeradvisor.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sandclub.beeradvisor.model.Beer;

@Database(entities = {Beer.class}, version = DATABASE_VERSION)
@TypeConverters(Converters.class)
abstract public class BeerRoomDatabase extends RoomDatabase{

    public abstract BeerDao beerDao();

    private static volatile BeerRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREAD = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREAD);

    public static BeerRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (BeerRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BeerRoomDatabase.class, BEER_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}
