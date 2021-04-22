package com.example.groundbooking;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ImageInfo> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<ImageInfo> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageInfo uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getFilename());
//        Picasso.with(mContext)
//                .load(uploadCurrent.getImageUrl())
//                .placeholder(R.mipmap.ic_launcher)
//                .fit()
//                .centerCrop()
//                .into(holder.imageview);
        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl()).centerCrop().placeholder(R.mipmap.ic_launcher).into(holder.imageview);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewName;
        public ImageView imageview;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageview = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(menu.NONE,1,1,"Do Whatever");
            MenuItem delete = menu.add(menu.NONE,2,1,"Delete");
            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch(item.getItemId()){
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;

                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener{
        void  onItemClick(int position);
        void  onWhatEverClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
