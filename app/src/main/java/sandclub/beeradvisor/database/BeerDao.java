package sandclub.beeradvisor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import sandclub.beeradvisor.model.Beer;

@Dao
public interface BeerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //aggiunge la birra, se ce un conflitto aggiorna
    void insertBeer(Beer beer);
    @Delete
    void deleteBeer(Beer beer);

    @Query("SELECT * FROM beer ORDER BY name ASC")
    List<Beer> getBeerOrderByName();
    @Query("SELECT * FROM beer WHERE id = :id")
    Beer getBeer(int id);

    @Query("SELECT * FROM beer")
    List<Beer> getAll();
}
