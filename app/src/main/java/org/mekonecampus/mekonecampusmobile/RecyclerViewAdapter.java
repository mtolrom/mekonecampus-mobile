package org.mekonecampus.mekonecampusmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Category> mData;

    public RecyclerViewAdapter(Context mContext, List<Category> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //get data
        holder.category_title_name.setText(mData.get(position).categoryName);
        holder.category_pic_id.setImageResource(mData.get(position).categoryPicture);
        holder.category_pic_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewActivity.class);
                intent.putExtra("Id", mData.get(position).categoryName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView category_pic_id;
        TextView category_title_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            category_pic_id = (ImageView) itemView.findViewById(R.id.category_pic_id);
            category_title_name = (TextView) itemView.findViewById(R.id.category_title_name);
        }
    }
}
