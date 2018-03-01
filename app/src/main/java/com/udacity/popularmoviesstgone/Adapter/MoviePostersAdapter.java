package com.udacity.popularmoviesstgone.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstgone.Model.Movies;
import com.udacity.popularmoviesstgone.R;

import java.util.List;

/*
 * Created by Aidan on 2/26/2018.
 */

public class MoviePostersAdapter extends BaseAdapter {
    private Context context;
    private List<Movies> posterList;
    public MoviePostersAdapter(Context context, List<Movies> posterList){
        this.context = context;
        this.posterList = posterList;
    }
    @Override
    public int getCount() {
        return posterList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movies movies = posterList.get(position);
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new GridView.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setContentDescription(movies.getMovieTitle());
        Picasso.with(context)
                .load(movies.getMoviePoster())
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.image_error)
                .into(imageView);
        return imageView;
    }
}
