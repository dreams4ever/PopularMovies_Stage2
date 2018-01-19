package com.example.dreams.popularmovies_stage2.adapters;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dreams.popularmovies_stage2.FilmDetailsFragment;
import com.example.dreams.popularmovies_stage2.R;
import com.example.dreams.popularmovies_stage2.activity.DetailsActivity;
import com.example.dreams.popularmovies_stage2.models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesPosterAdapter extends ArrayAdapter<MovieModel> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MovieModel> mList;
    boolean isTwoPane;
    private FragmentManager mFragmentManager;

    public MoviesPosterAdapter(Context c, FragmentManager fragmentManager, boolean isTwoPane) {
        super(c, R.layout.movie_list_item);
        mContext = c;
        this.isTwoPane = isTwoPane;
        mFragmentManager = fragmentManager;
        mInflater = LayoutInflater.from(c);
    }

    public void setData(List<MovieModel> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return (mList != null) ? mList.size() : 0;
    }

    @Override
    public MovieModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup viewGroup) {
        ViewHolder viewHolder;
        final View result;
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
                if (isTwoPane) {
                    Bundle args = new Bundle();
                    args.putInt("key", mList.get(position).getId());
                    FilmDetailsFragment fragment = new FilmDetailsFragment();
                    fragment.setArguments(args);
                    mFragmentManager.beginTransaction()
                            .replace(R.id.details_fragment, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(mContext, DetailsActivity.class);
                    intent.putExtra("key", mList.get(position).getId());
                    mContext.startActivity(intent);
                }


            }
        });
        return result;
    }

    private static class ViewHolder {
        TextView name;
        ImageView poster;
    }


}



