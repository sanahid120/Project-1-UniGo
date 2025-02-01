package unigo.bd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import unigo.bd.R;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

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

        // Handle long-click for menu
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.Delete_menu) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Are you sure?")
                                    .setMessage("Do you want to delete this schedule?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String category = ((Admin_Schedule) holder.itemView.getContext()).globalCatagory;
                                            // Use the correct item ID to remove it from Firebase
                                            FirebaseDatabase.getInstance().getReference().child(category)
                                                    .child(item.getId()) // Use the ID of the current schedule
                                                    .removeValue()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(holder.itemView.getContext(), "Schedule deleted", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(holder.itemView.getContext(), "Failed to delete schedule", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(holder.itemView.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();
                        }
                        else{
                            Intent intent = new Intent(holder.itemView.getContext(), UpdateSchedule.class);
                            intent.putExtra("SCHEDULE_ID", item.getId());
                            intent.putExtra("CATEGORY", ((Admin_Schedule) holder.itemView.getContext()).globalCatagory);
                            intent.putExtra("ROUTE", item.getRoute());
                            intent.putExtra("TIME", item.getTime());
                            intent.putExtra("BUS", item.getBus());
                            holder.itemView.getContext().startActivity(intent);
                        }
                        return false;
                    }
                });
                return false;
            }
        });

        // Set checkbox state
//        holder.cbMarkCompleted.setChecked(item.isMarkedCompleted());
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
