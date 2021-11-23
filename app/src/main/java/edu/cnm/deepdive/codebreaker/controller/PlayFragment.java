package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.adapter.GuessItemAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentPlayBinding;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.viewmodel.PlayViewModel;
import java.util.List;

public class PlayFragment extends Fragment {

  private PlayViewModel viewModel;
  private FragmentPlayBinding binding;
  private int codeLength;
  private Spinner[] spinners;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.submit.setOnClickListener((v) -> submitGuess());
    spinners = setupSpinners(
        binding.guessContainer, getResources().getInteger(R.integer.code_length_pref_max));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //noinspection ConstantConditions
    viewModel = new ViewModelProvider(getActivity()).get(PlayViewModel.class);
    getLifecycle().addObserver(viewModel);
    viewModel.getThrowable().observe(getViewLifecycleOwner(), this::displayError);
    viewModel.getGame().observe(getViewLifecycleOwner(), this::update);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.play_actions, menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled;
    if (item.getItemId() == R.id.new_game) {
      handled = true;
      viewModel.startGame();
    } else {
      handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void submitGuess() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < codeLength; i++) {
      String emoji = (String) spinners[i].getSelectedItem();
      builder.append(emoji);
    }
    viewModel.submitGuess(builder.toString());
  }

  private void update(Game game) {
    codeLength = game.getLength();
    String pool = game.getPool();
    List<Guess> guesses = game.getGuesses();
    Guess lastGuess = guesses.isEmpty() ? null : guesses.get(guesses.size() - 1);
    String[] emojis = getUnicodeArray(pool);
    updateSpinners(lastGuess, emojis);
    updateControls(game);
    updateGuesses(guesses);
  }

  private void updateGuesses(List<Guess> guesses) {
    GuessItemAdapter adapter = new GuessItemAdapter(getContext(), guesses);
    binding.guesses.setAdapter(adapter);
    binding.guesses.scrollToPosition(adapter.getItemCount() - 1);
  }

  private void updateControls(Game game) {
    if (game.isSolved()) {
      binding.guessContainer.setVisibility(View.GONE);
      binding.submit.setVisibility(View.GONE);
    } else {
      binding.guessContainer.setVisibility(View.VISIBLE);
      binding.submit.setVisibility(View.VISIBLE);
    }
  }

  private void updateSpinners(Guess lastGuess, String[] emojis) {
    for (int i = codeLength; i < spinners.length; i++) {
      spinners[i].setVisibility(View.GONE);
    }
    for (int spinnerIndex = 0; spinnerIndex < codeLength; spinnerIndex++) {
      spinners[spinnerIndex].setVisibility(View.VISIBLE);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_emoji, emojis);
      adapter.setDropDownViewResource(R.layout.item_emoji_pulldown);
      spinners[spinnerIndex].setAdapter(adapter);
      if (lastGuess != null) {
        String[] guessEmojis = getUnicodeArray(lastGuess.getText());
        String selection = guessEmojis[spinnerIndex];
        for (int emojiIndex = 0; emojiIndex < emojis.length; emojiIndex++) {
          if (emojis[emojiIndex].equals(selection)) {
            spinners[spinnerIndex].setSelection(emojiIndex);
            break;
          }
        }
      }
    }
  }

  private void displayError(Throwable throwable) {
    if (throwable != null) {
      Snackbar snackbar = Snackbar.make(binding.getRoot(),
          getString(R.string.play_error_message, throwable.getMessage()),
          Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction(R.string.error_dismiss, (v) -> snackbar.dismiss());
      snackbar.show();
    }
  }

  private Spinner[] setupSpinners(ConstraintLayout layout, int numSpinners) {
    Spinner[] spinners = new Spinner[numSpinners];
    LayoutInflater layoutInflater = getLayoutInflater();
    for (int i = 0; i < spinners.length; i++) {
      Spinner spinner = (Spinner) layoutInflater.inflate(R.layout.spinner_emoji, layout, false);
      layout.addView(spinner);
      spinner.setId(View.generateViewId());
      spinners[i] = spinner;
    }
    int layoutId = layout.getId();
    ConstraintSet constraints = new ConstraintSet();
    constraints.clone(layout);
    for (int i = 0; i < spinners.length; i++) {
      Spinner spinner = spinners[i];
      int spinnerId = spinner.getId();
      constraints.connect(
          spinnerId, ConstraintSet.START,
          (i > 0) ? spinners[i - 1].getId() : layoutId,
          (i > 0) ? ConstraintSet.END : ConstraintSet.START
      );
      constraints.connect(
          spinnerId, ConstraintSet.END,
          (i < spinners.length - 1) ? spinners[i + 1].getId() : layoutId,
          (i < spinners.length - 1) ? ConstraintSet.START : ConstraintSet.END
      );
      constraints.connect(spinnerId, ConstraintSet.TOP, layoutId, ConstraintSet.TOP);
      constraints.connect(spinnerId, ConstraintSet.BOTTOM, layoutId, ConstraintSet.BOTTOM);
    }
    constraints.applyTo(layout);
    return spinners;
  }

  private String[] getUnicodeArray(String source) {
    return source
        .codePoints()
        .mapToObj((codePoint) -> new String(new int[]{codePoint}, 0, 1))
        .toArray(String[]::new);
  }


}
