package edu.cnm.deepdive.codebreaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.codebreaker.databinding.ItemGameSummaryBinding;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;

public class GameSummaryAdapter extends RecyclerView.Adapter<GameSummaryAdapter.Holder> {

  private final LayoutInflater inflater;
  private final DateFormat dateFormat;
  private final NumberFormat numberFormat;
  private final List<GameSummary> games;

  public GameSummaryAdapter(Context context, List<GameSummary> games) {
    inflater = LayoutInflater.from(context);
    dateFormat = android.text.format.DateFormat.getDateFormat(context);
    numberFormat = NumberFormat.getIntegerInstance();
    this.games = games;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(ItemGameSummaryBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return games.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    private final ItemGameSummaryBinding binding;

    private Holder(@NonNull ItemGameSummaryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      GameSummary game = games.get(position);
      binding.created.setText(dateFormat.format(game.getCreated()));
      binding.totalTime.setText(numberFormat.format(Math.round(game.getTotalTime() / 1000.0)));
      binding.guessCount.setText(numberFormat.format(game.getGuessCount()));
    }

  }

}
