package com.udacity.popularmoviesstgone.Adapter;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstgone.Model.Movies;
import com.udacity.popularmoviesstgone.R;

import java.util.List;

import static com.udacity.popularmoviesstgone.MainActivity.POSTER_PATH_BASE_URL;

/*
 * Created by Aidan on 2/26/2018.
 */

public class MoviePostersAdapter extends BaseAdapter implements Parcelable {
    private Context context;
    private List<Movies> posterList;
    public MoviePostersAdapter(Context context, List<Movies> posterList){
        this.context = context;
        this.posterList = posterList;
    }

    private MoviePostersAdapter(Parcel in) {
        posterList = in.createTypedArrayList(Movies.CREATOR);
    }

    public static final Creator<MoviePostersAdapter> CREATOR = new Creator<MoviePostersAdapter>() {
        @Override
        public MoviePostersAdapter createFromParcel(Parcel in) {
            return new MoviePostersAdapter(in);
        }

        @Override
        public MoviePostersAdapter[] newArray(int size) {
            return new MoviePostersAdapter[size];
        }
    };

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
            imageView.setLayoutParams(new GridView.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setContentDescription(movies.getMovieTitle());
        String posterUrl = movies.getMoviePoster();
        if (!URLUtil.isValidUrl(movies.getMoviePoster())) {
            posterUrl = POSTER_PATH_BASE_URL.concat(posterUrl);
        }
        Picasso.with(context)
                .load(posterUrl)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.image_error)
                .into(imageView);
        return imageView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(posterList);
    }
}
