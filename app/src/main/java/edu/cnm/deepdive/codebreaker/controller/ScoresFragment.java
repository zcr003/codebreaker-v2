package edu.cnm.deepdive.codebreaker.controller;

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
import edu.cnm.deepdive.codebreaker.adapter.GameSummaryAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentScoresBinding;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import edu.cnm.deepdive.codebreaker.viewmodel.ScoresViewModel;
import java.util.List;
import java.util.function.BiConsumer;

public class ScoresFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

  private ScoresViewModel viewModel;
  private FragmentScoresBinding binding;
  private BiConsumer<Integer, Boolean> codeLengthUpdater;
  private BiConsumer<Integer, Boolean> poolSizeUpdater;
  private BiConsumer<Boolean, Boolean> sortedByTimeUpdater;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentScoresBinding.inflate(inflater, container, false);
    // TODO Add onClickListener to column headers. Listeners should invoke the set methods in
    //  ScoresViewModel to force a refresh of the query.
    // TODO Add onItemSelectedListener to spinner. Listener will invoke set methods in
    //  ScoresViewModel to force a refresh of the query.
    // TODO Populate spinners, using min and max code length & pool size integer resources.
    setupParameterChangeConsumers();
    binding.codeLength.setTag(codeLengthUpdater);
    binding.poolSize.setTag(poolSizeUpdater);
    binding.codeLength.setOnSeekBarChangeListener(this);
    binding.poolSize.setOnSeekBarChangeListener(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ScoresViewModel.class);
    viewModel
        .getScoreboard()
        .observe(getViewLifecycleOwner(), (games) -> {
          GameSummaryAdapter adapter = new GameSummaryAdapter(getContext(), games);
          binding.games.setAdapter(adapter);
        });
    viewModel
        .getCodeLength()
        .observe(getViewLifecycleOwner(), (codeLength) -> {/* TODO Set value of spinner. */});
    viewModel
        .getPoolSize()
        .observe(getViewLifecycleOwner(), (poolSize) -> {/* TODO Set value of spinner. */});
    viewModel
        .getSortedByTime()
        .observe(getViewLifecycleOwner(), (sortedByTime) -> {/* TODO Update styling & listeners on column headers. */});
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void setupParameterChangeConsumers() {
    codeLengthUpdater = (value, fromUser) -> {
      binding.codeLengthDisplay.setText(String.valueOf(value));
      if (fromUser) {
        viewModel.setCodeLength(value);
      }
    };
    poolSizeUpdater = (value, fromUser) -> {
      binding.poolSizeDisplay.setText(String.valueOf(value));
      if (fromUser) {
        viewModel.setPoolSize(value);
      }
    };
    sortedByTimeUpdater = (sortedByTime, fromUser) -> {
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
    };
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    ((BiConsumer<Integer, Boolean>) seekBar.getTag()).accept(progress, fromUser);
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
  }

}