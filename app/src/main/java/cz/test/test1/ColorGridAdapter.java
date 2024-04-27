package cz.test.test1;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ColorGridAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mImageResources;
    private int mCellSizeDP;
    private MainActivity mActivity;
    private boolean mIsColorButton;

    public ColorGridAdapter(Context context, int[] imageResources, int cellSizeDP, MainActivity activity, boolean isColorButton) {
        mContext = context;
        mImageResources = imageResources;
        mCellSizeDP = cellSizeDP;
        mActivity = activity;
        mIsColorButton = isColorButton;
    }

    @Override
    public int getCount() {
        return mImageResources.length;
    }

    @Override
    public Object getItem(int position) {
        return mImageResources[position];
    }

    @Override
    public long getItemId(int position) {
        return position; // Using position as the item ID
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageButton imageButton;
        if (convertView == null) {
            imageButton = new ImageButton(mContext);
            int cellSizePX = dpToPx(mCellSizeDP); // Convert dp to pixels
            imageButton.setLayoutParams(new GridView.LayoutParams(cellSizePX, cellSizePX));
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageButton.setBackgroundColor(Color.TRANSPARENT);
        } else {
            imageButton = (ImageButton) convertView;
        }

        // Set click listener for each view
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call methods from MainActivity based on button type
                if (mIsColorButton) {
                    mActivity.handleColorClick(position);
                } else {
                    mActivity.handleNumberClick(position);
                }
            }
        });

        // Load image using Glide and resize it to 200x200dp
        Glide.with(mContext)
                .load(mImageResources[position])
                .into(imageButton);

        return imageButton;
    }

    public int dpToPx(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


}
