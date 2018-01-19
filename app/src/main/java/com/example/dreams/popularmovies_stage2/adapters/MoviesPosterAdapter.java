package com.example.dreams.popularmovies_stage2.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.activity.DetailsActivity;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MoviesPosterAdapter extends ArrayAdapter<MovieModel> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MovieModel> mList;

    public MoviesPosterAdapter(Context c) {
        super(c, R.layout.movie_list_item);
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public void setData(List<MovieModel> list) {
        mList=list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return (mList != null) ? mList.size(): 0;
    }

    @Override
    public MovieModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final View result;
        final int i = position;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.movie_list_item, viewGroup, false);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.poster = view.findViewById(R.id.iv_poster);
            result = view;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }
        viewHolder.name.setText(mList.get(position).getOriginalTitle());
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w500" + mList.get(position).getPosterPath()).into(viewHolder.poster);
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("key", mList.get(i).getId());
                mContext.startActivity(intent);


            }
        });
        return result;
    }

    private static class ViewHolder {
        TextView name;
        ImageView poster;
    }


}



