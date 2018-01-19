package com.example.dreams.popularmovies_stage2.adapters;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.models.TrailersModel;

import java.util.List;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<TrailersModel> mList;

    public TrailersAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public void setData(List<TrailersModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trailers_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int i = position;
        holder.name.setText(mList.get(position).getname());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchYoutubeVideo(mList.get(i).getkey());
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        else
            return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView img;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trailer_name);
            img = itemView.findViewById(R.id.imgPlay);
        }
    }

    public void watchYoutubeVideo(String key) {
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            mContext.startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(browserIntent);
        }
    }

}

