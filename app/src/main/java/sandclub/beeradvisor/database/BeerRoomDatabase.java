package sandclub.beeradvisor.database;

import static sandclub.beeradvisor.util.Constants.DATABASE_VERSION;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import sandclub.beeradvisor.model.Beer;

@Database(entities = {Beer.class}, version = DATABASE_VERSION)
@TypeConverters(Converters.class)
abstract public class BeerRoomDatabase extends RoomDatabase{

    public abstract BeerDao beerDao();
}
