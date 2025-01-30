package unigo.bd;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Adapter_UserSchedule extends RecyclerView.Adapter<Adapter_UserSchedule.ScheduleViewHolder> {

    private final List<ScheduleItem> scheduleList;

    public Adapter_UserSchedule(List<ScheduleItem> scheduleList) {
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
