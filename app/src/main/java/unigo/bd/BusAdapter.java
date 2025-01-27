package unigo.bd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private final List<String> busList; // Holds the bus numbers
    private final OnItemClickListener listener; // Listener for item clicks

    // Constructor to initialize busList and listener
    public BusAdapter(List<String> busList, OnItemClickListener listener) {
        this.busList = busList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item, parent, false);
        return new BusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        // Bind the data (bus number) to the ViewHolder
        String busNumber = busList.get(position);
        holder.busNumberTextView.setText(busNumber);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(busNumber);
            }
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
        void onItemClick(String busNumber);
    }
}