package edu.cnm.deepdive.codebreaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.databinding.ItemRankBinding;
import edu.cnm.deepdive.codebreaker.model.dto.RankedUser;
import java.text.NumberFormat;
import java.util.List;

public class RankedUserAdapter extends RecyclerView.Adapter<RankedUserAdapter.Holder> {

  private static final int SECONDS_PER_MINUTE = 60;
  private static final int MILLISECONDS_PER_SECOND = 1000;
  private static final int MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE;

  private final LayoutInflater inflater;
  private final String elapsedTimeFormat;
  private final NumberFormat numberFormat;
  private final List<RankedUser> users;

  public RankedUserAdapter(Context context, List<RankedUser> users) {
    inflater = LayoutInflater.from(context);
    elapsedTimeFormat = context.getString(R.string.elapsed_time_format);
    numberFormat = NumberFormat.getNumberInstance();
    this.users = users;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(ItemRankBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return users.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    private final ItemRankBinding binding;

    private Holder(@NonNull ItemRankBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      RankedUser user = users.get(position);
      binding.displayName.setText(user.getDisplayName());
      binding.guessCount.setText(numberFormat.format(user.getAverageGuessCount()));
      long minutes = (long) Math.floor(user.getAverageTime() / MILLISECONDS_PER_MINUTE);
      double seconds = (user.getAverageTime() % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND;
      binding.totalTime.setText(String.format(elapsedTimeFormat, minutes, seconds));
    }

  }

}
