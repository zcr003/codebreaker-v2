package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.os.TokenWatcher;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.adapter.GuessItemAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentPlayBinding;
import edu.cnm.deepdive.codebreaker.viewmodel.MainViewModel;

public class PlayFragment extends Fragment implements InputFilter {

  private static final String ILLEGAL_CHARACTERS_FORMAT = "[^%s]+";

  private MainViewModel viewModel;
  private FragmentPlayBinding binding;
  private int codeLength;
  private String pool;
  private String illegalCharacters;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.submit.setOnClickListener((v) ->
        viewModel.submitGuess(binding.guess.getText().toString().trim()));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getThrowable().observe(getViewLifecycleOwner(), (throwable) -> {
      if (throwable != null) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
      }
    });
    viewModel.getGame().observe(getViewLifecycleOwner(), (game) -> {
      GuessItemAdapter adapter = new GuessItemAdapter(getContext(), game.getGuesses());
      binding.guesses.setAdapter(adapter);
      binding.guessContainer.setVisibility(game.isSolved() ? View.GONE : View.VISIBLE);
      codeLength = game.getLength();
      pool = game.getPool();
      illegalCharacters = String.format(ILLEGAL_CHARACTERS_FORMAT, pool);
      // TODO Enforce submit conditions.
    });
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

  @Override
  public CharSequence filter(
      CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
    String modifiedSource = source
        .subSequence(start, end)
        .toString()
        .toUpperCase()
        .replaceAll(illegalCharacters, "");
    StringBuilder builder = new StringBuilder(dest);
    builder.replace(dstart, dend, modifiedSource);
    if (builder.length() > codeLength) {
      modifiedSource =
          modifiedSource.substring(0, modifiedSource.length() - (builder.length() - codeLength));
    }
    int newLength = dest.length() - (dend - dstart) + modifiedSource.length();
    binding.submit.setEnabled(newLength == codeLength);
    return modifiedSource;
  }
}