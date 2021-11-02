package edu.cnm.deepdive.codebreaker.service;

import androidx.annotation.NonNull;
import edu.cnm.deepdive.codebreaker.model.dao.GameDao;
import edu.cnm.deepdive.codebreaker.model.dao.GuessDao;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class GameRepository {

  private final WebServiceProxy proxy;
  private final GameDao gameDao;
  private final GuessDao guessDao;

  public GameRepository() {
    proxy = WebServiceProxy.getInstance();
    CodebreakerDatabase database = CodebreakerDatabase.getInstance();
    gameDao = database.getGameDao();
    guessDao = database.getGuessDao();
  }

  public Single<Game> startGame(String pool, int length) {
    return Single
        .fromCallable(() -> {
          Game game = new Game();
          game.setPool(pool);
          game.setLength(length);
          return game;
        })
        .flatMap(proxy::startGame)
        .subscribeOn(Schedulers.io());
  }

  public Single<Game> submitGuess(Game game, String text) {
    return Single
        .fromCallable(() -> {
          Guess guess = new Guess();
          guess.setText(text);
          return guess;
        })
        .flatMap((guess) -> proxy.submitGuess(guess, game.getServiceKey()))
        .map((guess) -> {
          game.getGuesses().add(guess);
          game.setSolved(guess.isSolution());
          return game;
        })
        .flatMap(this::insertGameWithGuesses)
        .subscribeOn(Schedulers.io());
  }

  @NonNull
  private Single<Game> insertGameWithGuesses(Game game) {
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
              // TODO invoke Guess.setId for all of the guesses.
              .map((ids) -> g2))
    : Single.just(game);
  }

}
