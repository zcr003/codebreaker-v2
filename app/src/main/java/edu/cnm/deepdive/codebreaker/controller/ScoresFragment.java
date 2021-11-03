package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.adapter.GameSummaryAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentScoresBinding;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import edu.cnm.deepdive.codebreaker.viewmodel.ScoresViewModel;
import java.util.List;

public class ScoresFragment extends Fragment {

  private ScoresViewModel viewModel;
  private FragmentScoresBinding binding;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentScoresBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ScoresViewModel.class);
    viewModel
        .getGames()
        .observe(getViewLifecycleOwner(), (games) -> {
          GameSummaryAdapter adapter = new GameSummaryAdapter(getContext(), games);
          binding.games.setAdapter(adapter);
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

}