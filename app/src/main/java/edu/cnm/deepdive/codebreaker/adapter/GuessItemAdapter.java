package edu.cnm.deepdive.codebreaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.cnm.deepdive.codebreaker.adapter.GuessItemAdapter.Holder;
import edu.cnm.deepdive.codebreaker.databinding.ItemGuessBinding;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import java.util.List;

public class GuessItemAdapter extends Adapter<Holder> {

  private final Context context;
  private final List<Guess> guesses;
  private final LayoutInflater inflater;

  public GuessItemAdapter(Context context, List<Guess> guesses) {
    this.context = context;
    this.guesses = guesses;
    inflater = LayoutInflater.from(context);
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
      binding.text.setText(guess.getText());
      binding.exactMatches.setText(String.valueOf(guess.getExactMatches()));
      binding.nearMatches.setText(String.valueOf(guess.getNearMatches()));
    }

  }

}
