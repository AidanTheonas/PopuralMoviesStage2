package com.udacity.popularmoviesstgone.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieReviews implements Parcelable {
    private String reviewAuthor, reviewComments, reviewUrl;

    public MovieReviews() {
    }

    public MovieReviews(String reviewAuthor, String reviewComments, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewComments = reviewComments;
        this.reviewUrl = reviewUrl;
    }

    private MovieReviews(Parcel in) {
        reviewAuthor = in.readString();
        reviewComments = in.readString();
        reviewUrl = in.readString();
    }

    public static final Creator<MovieReviews> CREATOR = new Creator<MovieReviews>() {
        @Override
        public MovieReviews createFromParcel(Parcel in) {
            return new MovieReviews(in);
        }

        @Override
        public MovieReviews[] newArray(int size) {
            return new MovieReviews[size];
        }
    };

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewAuthor);
        dest.writeString(reviewComments);
        dest.writeString(reviewUrl);
    }
}
