package edu.cnm.deepdive.codebreaker.controller;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.adapter.RankedUserAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentRanksBinding;
import edu.cnm.deepdive.codebreaker.viewmodel.ScoresViewModel;

public class RanksFragment extends Fragment {

  private ScoresViewModel viewModel;
  private FragmentRanksBinding binding;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentRanksBinding.inflate(inflater, container, false);
    Resources resources = getResources();
    binding.codeLengthDisplay.setText(String.valueOf(
        resources.getInteger(R.integer.code_length_pref_default)));
    binding.poolSizeDisplay.setText(String.valueOf(
        resources.getInteger(R.integer.pool_size_pref_default)));
    binding.codeLength.setOnSeekBarChangeListener(
        (SimpleChangeListener) this::handleCodeLengthChange);
    binding.poolSize.setOnSeekBarChangeListener((SimpleChangeListener) this::handlePoolSizeChange);
    binding.header.time.setOnClickListener((v) -> handleSortedByTimeChange(true, true));
    binding.header.guesses.setOnClickListener((v) -> handleSortedByTimeChange(false, true));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ScoresViewModel.class);
    viewModel
        .getRankings()
        .observe(getViewLifecycleOwner(), (ranks) -> {
          RankedUserAdapter adapter = new RankedUserAdapter(getContext(), ranks);
          binding.ranks.setAdapter(adapter);
        });
    viewModel
        .getCodeLength()
        .observe(getViewLifecycleOwner(),
            (codeLength) -> binding.codeLength.setProgress(codeLength));
    viewModel
        .getPoolSize()
        .observe(getViewLifecycleOwner(), (poolSize) -> binding.poolSize.setProgress(poolSize));
    viewModel
        .getSortedByTime()
        .observe(getViewLifecycleOwner(),
            (sortedByTime) -> handleSortedByTimeChange(sortedByTime, false));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void handleCodeLengthChange(View view, int value, boolean fromUser) {
    binding.codeLengthDisplay.setText(String.valueOf(value));
    if (fromUser) {
      viewModel.setCodeLength(value);
    }
  }

  private void handlePoolSizeChange(View view, int value, boolean fromUser) {
    binding.poolSizeDisplay.setText(String.valueOf(value));
    if (fromUser) {
      viewModel.setPoolSize(value);
    }
  }

  private void handleSortedByTimeChange(boolean sortedByTime, boolean fromUser) {
    if (sortedByTime) {
      binding.header.time.setText(R.string.time_header_selected);
      binding.header.guesses.setText(R.string.guesses_header_unselected);
    } else {
      binding.header.time.setText(R.string.time_header_unselected);
      binding.header.guesses.setText(R.string.guesses_header_selected);
    }
    if (fromUser) {
      viewModel.setSortedByTime(sortedByTime);
    }
  }

  @FunctionalInterface
  private interface SimpleChangeListener extends SeekBar.OnSeekBarChangeListener {

    @Override
    default void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    default void onStopTrackingTouch(SeekBar seekBar) {
    }

  }
}