package com.udacity.popularmoviesstgone.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieTrailers implements Parcelable {
    private String trailerID, trailerDescription;

    public MovieTrailers() {
    }

    public MovieTrailers(String trailerID, String trailerDescription) {
        this.trailerID = trailerID;
        this.trailerDescription = trailerDescription;
    }

    private MovieTrailers(Parcel in) {
        trailerID = in.readString();
        trailerDescription = in.readString();
    }

    public static final Creator<MovieTrailers> CREATOR = new Creator<MovieTrailers>() {
        @Override
        public MovieTrailers createFromParcel(Parcel in) {
            return new MovieTrailers(in);
        }

        @Override
        public MovieTrailers[] newArray(int size) {
            return new MovieTrailers[size];
        }
    };

    public String getTrailerDescription() {
        return trailerDescription;
    }

    public void setTrailerDescription(String trailerDescription) {
        this.trailerDescription = trailerDescription;
    }

    public String getTrailerID() {
        return trailerID;
    }

    public void setTrailerID(String trailerID) {
        this.trailerID = trailerID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerID);
        dest.writeString(trailerDescription);
    }
}
