package sandclub.beeradvisor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import sandclub.beeradvisor.model.Beer;

@Dao
public interface BeerDao {
    @Insert
    void insertAll(Beer... beer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBeerList(List<Beer> beerList);
    @Delete
    void deleteBeer(Beer beer);

    @Query("SELECT * FROM beer ORDER BY nome ASC")
    List<Beer> getBeerOrderByName();
    @Query("SELECT * FROM beer WHERE id = :id")
    Beer getBeer(int id);

    @Query("SELECT * FROM beer")
    List<Beer> getAll();

    @Query("SELECT * FROM beer ORDER BY RANDOM() LIMIT 10")
    List<Beer>getRandomBeer();

    @Update
    int updateSingleFavoriteBeer(Beer beer);

    @Query("SELECT * FROM beer WHERE is_favorite = 1 ")
    List<Beer> getFavoriteBeer();
    @Query("DELETE FROM beer")
    int deleteAll();


}
