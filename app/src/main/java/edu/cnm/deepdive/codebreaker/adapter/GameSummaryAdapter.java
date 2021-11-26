package edu.cnm.deepdive.codebreaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.databinding.ItemGameSummaryBinding;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class GameSummaryAdapter extends RecyclerView.Adapter<GameSummaryAdapter.Holder> {

  private static final int SECONDS_PER_MINUTE = 60;
  private static final int MILLISECONDS_PER_SECOND = 1000;
  private static final int MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE;

  private final LayoutInflater inflater;
  private final DateFormat dateFormat;
  private final DateFormat timeFormat;
  private final String dateTimeCombinationFormat;
  private final String elapsedTimeFormat;
  private final NumberFormat numberFormat;
  private final List<GameSummary> games;

  public GameSummaryAdapter(Context context, List<GameSummary> games) {
    inflater = LayoutInflater.from(context);
    dateFormat = android.text.format.DateFormat.getDateFormat(context);
    timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    dateTimeCombinationFormat = context.getString(R.string.date_time_combination_format);
    elapsedTimeFormat = context.getString(R.string.elapsed_time_format);
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
      Date created = game.getCreated();
      binding.created.setText(String.format(dateTimeCombinationFormat,
          dateFormat.format(created), timeFormat.format(created)));
      binding.guessCount.setText(numberFormat.format(game.getGuessCount()));
      long minutes = game.getTotalTime() / MILLISECONDS_PER_MINUTE;
      float seconds =
          (game.getTotalTime() % MILLISECONDS_PER_MINUTE) / (float) MILLISECONDS_PER_SECOND;
      binding.totalTime.setText(String.format(elapsedTimeFormat, minutes, seconds));
    }

  }

}
