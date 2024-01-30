package sandclub.beeradvisor.database;

import static sandclub.beeradvisor.util.Constants.BEER_DATABASE_NAME;
import static sandclub.beeradvisor.util.Constants.DATABASE_VERSION;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sandclub.beeradvisor.model.Beer;

@Database(entities = {Beer.class}, version = 3)
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
                            BeerRoomDatabase.class, BEER_DATABASE_NAME).addMigrations(MIGRATION_2_3).build();
                }
            }
        }
        return INSTANCE;
    }

    /*static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Aggiungi il nuovo campo booleano "preferito" alla tabella Beer
            database.execSQL("ALTER TABLE beer ADD COLUMN is_favorite INTEGER NOT NULL DEFAULT 0 ");
        }
    };*/

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Aggiungi il nuovo campo booleano "preferito" alla tabella Beer
            database.execSQL("ALTER TABLE beer ADD COLUMN is_synchronized INTEGER NOT NULL DEFAULT 0");
        }
    };
}
