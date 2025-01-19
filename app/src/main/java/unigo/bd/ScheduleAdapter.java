package unigo.bd;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<ScheduleItem> scheduleList;

    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleItem item = scheduleList.get(position);
        holder.tvRoute.setText(item.getRoute());
        holder.tvTime.setText(item.getTime());
        holder.tvBus.setText(item.getBus());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvTime, tvBus;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBus = itemView.findViewById(R.id.tvBus);
        }
    }
}