package edu.cnm.deepdive.codebreaker.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import io.reactivex.Single;
import java.util.Collection;
import java.util.List;

@Dao
public interface GameDao {

  @Insert
  Single<Long> insert(Game game);

  @Insert
  Single<List<Long>> insert(Game... games);

  @Insert
  Single<List<Long>> insert(Collection<Game> games);

  @Update
  Single<Integer> update(Game game);

  @Update
  Single<Integer> update(Game... games);

  @Update
  Single<Integer> update(Collection<Game> games);

  @Delete
  Single<Integer> delete(Game game);

  @Delete
  Single<Integer> delete(Game... games);

  @Delete
  Single<Integer> delete(Collection<Game> games);

  @Query("SELECT * FROM game ORDER BY created DESC")
  LiveData<List<Game>> selectAll();

  @Query("SELECT * FROM game WHERE game_id = :gameId")
  LiveData<Game> select(long gameId);

  @Query("SELECT * FROM game_summary WHERE pool_size = :poolSize AND length = :length ORDER BY guess_count ASC")
  LiveData<List<GameSummary>> selectSummariesByGuessCount(int poolSize, int length);

  @Query("SELECT * FROM game_summary WHERE pool_size = :poolSize AND length = :length ORDER BY total_time ASC")
  LiveData<List<GameSummary>> selectSummariesByTotalTime(int poolSize, int length);

}
