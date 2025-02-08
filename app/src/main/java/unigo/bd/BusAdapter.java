package unigo.bd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private final List<Bus> busList; // Holds Bus objects

    // Constructor to initialize busList and listener
    public BusAdapter(ArrayList<Bus> busList) {
        this.busList = busList;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        // Bind the Bus object to the ViewHolder
        Bus bus = busList.get(position);
        holder.busNumberTextView.setText(bus.getBusNumber()); // Display bus number

        // Handle long-click for popup menu
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), v);
            popupMenu.inflate(R.menu.popup_menu); // Inflate menu
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.Delete_menu) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Are you sure?")
                            .setMessage("Do you want to delete this bus?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                FirebaseDatabase.getInstance().getReference("buses")
                                        .child(bus.getId()) // Use the Bus ID for deletion
                                        .removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(holder.itemView.getContext(), "Bus deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(holder.itemView.getContext(), "Failed to delete bus", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                else {
                    // Show dialog for editing
                    final EditText input = new EditText(holder.itemView.getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(bus.getBusNumber()); // Pre-fill the current bus number

                    AlertDialog.Builder editBuilder = new AlertDialog.Builder(holder.itemView.getContext());
                    editBuilder.setTitle("Edit Bus Number")
                            .setView(input)
                            .setPositiveButton("Update", (dialog, which) -> {
                                String updatedBusNumber = input.getText().toString().trim();
                                if (!updatedBusNumber.isEmpty()) {
                                    // Update bus number in Firebase

                                    Bus newBus =new Bus(updatedBusNumber);
                                    newBus.setId(bus.getId());
                                    FirebaseDatabase.getInstance().getReference("buses")
                                            .child(bus.getId()) // Use the Bus ID to update
                                            .setValue(newBus) // Update the bus object
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(holder.itemView.getContext(), "Bus updated", Toast.LENGTH_SHORT).show();
                                                    // Update the local list and notify the adapter
                                                    bus.setBusNumber(updatedBusNumber);
                                                    notifyItemChanged(holder.getAdapterPosition());
                                                } else {
                                                    Toast.makeText(holder.itemView.getContext(), "Failed to update bus", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(holder.itemView.getContext(), "Bus number cannot be empty!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                return false;
            });
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return busList.size(); // Returns the total number of items
    }

    // ViewHolder class to hold the views for each item
    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView busNumberTextView;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumberTextView = itemView.findViewById(R.id.busNumberTextView); // Reference to TextView in the layout
        }
    }

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(Bus bus); // Pass the Bus object instead of String
    }
}
