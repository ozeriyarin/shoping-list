package com.example.ex2.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ex2.R;
import com.example.ex2.activities.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomeAdapter extends RecyclerView.Adapter<CustomeAdapter.ViewHolder> implements Filterable {

//    public interface OnItemClickListener {
//        void onItemClick(Product item);
//    }
//
//    private OnItemClickListener listener;
//    public void setOnItemClickListener(OnItemClickListener l) {
//        this.listener = l;
//    }

    private List<Product> fullList = new ArrayList<>();     // ALL data
    private List<Product> filteredList = new ArrayList<>(); // what's shown

    public CustomeAdapter(ArrayList<Product> productList) { setData(productList);}

    public void setData(List<Product> data) {
        fullList.clear();
        fullList.addAll(data);

        filteredList.clear();
        filteredList.addAll(data);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        return new ViewHolder(itemView);
    }

    /** remove item and return it (for UNDO) */
    public Product removeItem(int position) {
        Product removed = filteredList.remove(position);
        fullList.remove(removed);            // keep the master list in‑sync
        notifyItemRemoved(position);
        return removed;
    }

    /** restore an item at a given position (used by Snackbar‑UNDO) */
    public void restoreItem(Product item, int position) {
        fullList.add(position, item);
        filteredList.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = filteredList.get(position);
        holder.name.setText(item.getName());
        holder.description.setText("Description: " + item.getDescription());
        holder.quantity.setText("Quantity: " + String.valueOf(item.getQuantity()));

        if (item.getImageUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }

    }

    @Override public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<Product> results = new ArrayList<>();
                if (query.isEmpty()) {
                    results.addAll(fullList);
                } else {
                        for (Product item : fullList) {
                        if (item.getName().toLowerCase().contains(query)) {
                            results.add(item);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = results;
                return fr;
            }
            @Override @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults  fr) {
                filteredList.clear();
                filteredList.addAll((List<Product>) fr.values);
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, description , quantity;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.textProduct);
            description = itemView.findViewById(R.id.textDescription);
            quantity = itemView.findViewById(R.id.textQty);
        }
    }
}
