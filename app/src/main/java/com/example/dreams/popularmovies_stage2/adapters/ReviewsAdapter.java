package com.example.dreams.popularmovies_stage2.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.models.ReviewsModel;

import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ReviewsModel> mList;

    public ReviewsAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }
    public void setData(List<ReviewsModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.reviews_list_item, parent, false);
        return new ReviewsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int i = position;
        holder.author.setText(mList.get(i).getAuthor());
        holder.content.setText(mList.get(i).getContent());

    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        else
            return mList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView content;

        MyViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            content = itemView.findViewById(R.id.content);
        }
    }
}
