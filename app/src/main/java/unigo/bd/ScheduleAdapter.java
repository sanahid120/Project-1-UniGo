package unigo.bd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<ScheduleItem> scheduleList;
    private final OnScheduleActionListener actionListener;

    public interface OnScheduleActionListener {
        void onMarkCompleted(ScheduleItem scheduleItem, boolean isChecked);
        void onDeleteSchedule(ScheduleItem scheduleItem);
    }

    public ScheduleAdapter(List<ScheduleItem> scheduleList, OnScheduleActionListener actionListener) {
        this.scheduleList = scheduleList;
        this.actionListener = actionListener;
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

        // Set checkbox state
        holder.cbMarkCompleted.setChecked(item.isMarkedCompleted());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvTime, tvBus;
        CheckBox cbMarkCompleted;
        ImageButton btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBus = itemView.findViewById(R.id.tvBus);
            cbMarkCompleted = itemView.findViewById(R.id.cbMarkCompleted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
