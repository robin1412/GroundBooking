package com.example.userbooking;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ImageViewHolder>{
    private Context mContext;
    private List<SellerDetails> mUploads;
    private HomeAdapter.OnItemClickListener mListener;

    public HomeAdapter(Context context, List<SellerDetails> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public HomeAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new HomeAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ImageViewHolder holder, int position) {
        SellerDetails uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getGroundName());
        Glide.with(mContext)
                .load(uploadCurrent.getProfilePicLink()).centerCrop().placeholder(R.mipmap.ic_launcher).into(holder.imageview);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewName;
        public ImageView imageview;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name2);
            imageview = itemView.findViewById(R.id.image_view_upload2);

            itemView.setOnClickListener(this);
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



    }

    public interface OnItemClickListener{
        void  onItemClick(int position);

    }
    public void setOnItemClickListener(HomeAdapter.OnItemClickListener listener){
        mListener = listener;
    }
}