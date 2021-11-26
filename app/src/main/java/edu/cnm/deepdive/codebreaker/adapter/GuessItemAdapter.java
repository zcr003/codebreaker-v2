package edu.cnm.deepdive.codebreaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.adapter.GuessItemAdapter.Holder;
import edu.cnm.deepdive.codebreaker.databinding.ItemGuessBinding;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import java.util.List;

public class GuessItemAdapter extends Adapter<Holder> {

  private final List<Guess> guesses;
  private final LayoutInflater inflater;
  private final String guessNumberFormat;

  public GuessItemAdapter(Context context, List<Guess> guesses) {
    this.guesses = guesses;
    inflater = LayoutInflater.from(context);
    guessNumberFormat = context.getString(R.string.guess_number_format);
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(ItemGuessBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return guesses.size();
  }

  class Holder extends ViewHolder {

    private final ItemGuessBinding binding;
    
    private Holder(@NonNull ItemGuessBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      Guess guess = guesses.get(position);
      binding.guessNumber.setText(String.format(guessNumberFormat, position + 1));
      binding.text.setText(guess.getText());
      binding.exactMatches.setText(String.valueOf(guess.getExactMatches()));
      binding.nearMatches.setText(String.valueOf(guess.getNearMatches()));
    }

  }

}
