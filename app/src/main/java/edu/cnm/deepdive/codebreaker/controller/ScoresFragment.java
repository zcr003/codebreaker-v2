package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.viewmodel.ScoresViewModel;
import edu.cnm.deepdive.codebreaker.databinding.FragmentGalleryBinding;

public class ScoresFragment extends Fragment {

  private ScoresViewModel scoresViewModel;
  private FragmentGalleryBinding binding;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    scoresViewModel =
        new ViewModelProvider(this).get(ScoresViewModel.class);

    binding = FragmentGalleryBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    final TextView textView = binding.textGallery;
    scoresViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(@Nullable String s) {
        textView.setText(s);
      }
    });
    return root;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}