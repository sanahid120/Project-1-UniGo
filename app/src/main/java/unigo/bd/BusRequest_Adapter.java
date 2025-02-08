package unigo.bd;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusRequest_Adapter extends RecyclerView.Adapter<BusRequest_Adapter.ViewHolder> {
    private List<BusRequestModel> requestList;
    private boolean isAdmin;
    private Context context;
    private DatabaseReference dbRef;

    public BusRequest_Adapter(List<BusRequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        this.isAdmin = new SessionManager(context).getUserType().equals("admin");  // Get admin status
        this.dbRef = FirebaseDatabase.getInstance().getReference("BusRequests");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_bus_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusRequestModel request = requestList.get(position);
        holder.tvTime.setText(request.getTime());
        holder.tvRoute.setText(request.getRoute());
        holder.tvCount.setText(String.valueOf(request.getCount()));

        // Set long press listener for Admins only
        if (isAdmin) {
            holder.itemView.setOnLongClickListener(v -> {
                showPopupMenu(v, position, request);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvRoute, tvCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }

    // Show Popup Menu on long press
    private void showPopupMenu(View view, int position, BusRequestModel request) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Delete Request");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Delete Request")) {
                confirmDelete(position, request);
            }
            return true;
        });

        popupMenu.show();
    }

    // Confirm before deleting
    private void confirmDelete(int position, BusRequestModel request) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Request")
                .setMessage("Are you sure you want to delete this request?")
                .setPositiveButton("Yes", (dialog, which) -> deleteRequest(position, request))
                .setNegativeButton("No", null)
                .show();
    }

    // Delete request from Firebase and RecyclerView
    private void deleteRequest(int position, BusRequestModel request) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        dbRef.child(currentDate)
                .child(request.getTime())  // Select time slot
                .child(request.getRoute()) // Select route
                .removeValue()  // Remove from Firebase
                .addOnSuccessListener(v -> {
                    requestList.remove(position);  // Remove from RecyclerView
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Request deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to delete request: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
