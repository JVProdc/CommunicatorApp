package cz.test.test1;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> imagesList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener; // New interface for long click
    private ArrayList<String> imageLabels = new ArrayList<>();

    private boolean isNewImageAdded = false;


    public void addImageLabel(String label) {
        imageLabels.add(label);
    }

    public GalleryAdapter(Context context, ArrayList<String> imagesList,
                          OnItemClickListener listener, OnItemLongClickListener longClickListener, // Update constructor
                          ArrayList<String> imageLabels) {
        this.context = context;
        this.imagesList = imagesList;
        this.onItemClickListener = listener;
        this.onItemLongClickListener = longClickListener; // Initialize long click listener
        this.imageLabels = imageLabels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 0 && position < imagesList.size()) {
            String imagePath = imagesList.get(position);

            Picasso.get()
                    .load(new File(imagePath)) // Assuming imagePath is a file path
                    .fit()
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageButton);

            if (position < imageLabels.size()) {
                String label = imageLabels.get(position);
                holder.labelView.setText(label);
            } else {
                holder.labelView.setText("");
            }
        } else {
            Log.e("Main Activity", "Index out of bounds:" + position);
        }
    }


    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    // Interface for item click
    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }

    // Interface for item long click
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton imageButton;
        TextView labelView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.gallery_item);
            labelView = itemView.findViewById(R.id.labelView);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });

            // Set up long click listener for imageButton
            imageButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(position);
                        return true; // Indicate that the long click event is consumed
                    }
                    return false;
                }
            });
        }

        public void setLabel(String label) {
            labelView.setText(label);
        }
    }

    public void updateData(ArrayList<String> newImagesList, ArrayList<String> newImageLabels) {
        // Clear existing data
        imagesList.clear();
        imageLabels.clear();

        // Add new data
        imagesList.addAll(newImagesList);
        imageLabels.addAll(newImageLabels);

        // Notify the adapter of the data change
        //notifyDataSetChanged();
    }
}