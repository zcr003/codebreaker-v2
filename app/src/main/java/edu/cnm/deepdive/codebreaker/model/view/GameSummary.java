package edu.cnm.deepdive.codebreaker.model.view;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import edu.cnm.deepdive.codebreaker.model.entity.Game;

@DatabaseView(value = GameSummary.QUERY, viewName = "game_summary")
public class GameSummary extends Game {

  static final String QUERY = "SELECT \n"
      + "  g.*, \n"
      + "  s.guess_count, \n"
      + "  (s.last_guess - s.first_guess) AS total_time \n"
      + "FROM \n"
      + "  game AS g \n"
      + "  INNER JOIN ( \n"
      + "    SELECT \n"
      + "      game_id, \n"
      + "      COUNT(*) AS guess_count, \n"
      + "      MIN(created) AS first_guess, \n"
      + "      MAX(created) AS last_guess \n"
      + "    FROM \n"
      + "      guess \n"
      + "    GROUP BY \n"
      + "      game_id \n"
      + "      \n"
      + "  ) AS s \n"
      + "     ON g.game_id = s.game_id\n";

  @ColumnInfo(name = "guess_count")
  private int guessCount;

  @ColumnInfo(name = "total_time")
  private long totalTime;

  public int getGuessCount() {
    return guessCount;
  }

  public void setGuessCount(int guessCount) {
    this.guessCount = guessCount;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
  }

}
