package edu.cnm.deepdive.codebreaker.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.codebreaker.model.dao.GameDao;
import edu.cnm.deepdive.codebreaker.model.dao.GuessDao;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.pojo.GameWithGuesses;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Iterator;
import java.util.List;

public class GameRepository {

  private final WebServiceProxy proxy;
  private final GameDao gameDao;
  private final GuessDao guessDao;
  private final GoogleSignInRepository signInRepository;

  public GameRepository() {
    proxy = WebServiceProxy.getInstance();
    CodebreakerDatabase database = CodebreakerDatabase.getInstance();
    gameDao = database.getGameDao();
    guessDao = database.getGuessDao();
    signInRepository = GoogleSignInRepository.getInstance();
  }

  public Single<GameWithGuesses> save(GameWithGuesses game) {
    return signInRepository
        .refreshBearerToken()
        .flatMap((token) -> proxy.startGame(game, token))
        .map((startedGame) -> {
          int poolSize = (int) startedGame
              .getPool()
              .codePoints()
              .count();
          startedGame.setPoolSize(poolSize);
          return startedGame;
        })
        .subscribeOn(Schedulers.io());
  }

  public Single<GameWithGuesses> save(GameWithGuesses game, Guess guess) {
    return signInRepository
        .refreshBearerToken()
        .flatMap((token) -> proxy.submitGuess(
            guess, game.getServiceKey(), token))
        .map((processedGuess) -> {
          game.getGuesses().add(processedGuess);
          game.setSolved(processedGuess.isSolution());
          return game;
        })
        .flatMap(this::insertGameWithGuesses)
        .subscribeOn(Schedulers.io());
  }

  public LiveData<GameWithGuesses> get(long gameId) {
    return gameDao.select(gameId);
  }

  public LiveData<List<GameSummary>> getOrderedByGuessCount(int poolSize, int length) {
    return gameDao.selectSummariesByGuessCount(poolSize, length);
  }

  public LiveData<List<GameSummary>> getOrderedByTotalTime(int poolSize, int length) {
    return gameDao.selectSummariesByTotalTime(poolSize, length);
  }

  @NonNull
  private Single<GameWithGuesses> insertGameWithGuesses(GameWithGuesses game) {
    return (game.isSolved())
        ? gameDao
        .insert(game)
        .map((id) -> {
          game.setId(id);
          for (Guess guess : game.getGuesses()) {
            guess.setGameId(id);
          }
          return game;
        })
        .flatMap((g2) -> guessDao
            .insert(g2.getGuesses())
            .map((ids) -> {
              Iterator<Long> idIterator = ids.iterator();
              Iterator<Guess> guessIterator = g2.getGuesses().iterator();
              while (idIterator.hasNext() && guessIterator.hasNext()) {
                guessIterator.next().setId(idIterator.next());
              }
              return g2;
            }))
        : Single.just(game);
  }

}
