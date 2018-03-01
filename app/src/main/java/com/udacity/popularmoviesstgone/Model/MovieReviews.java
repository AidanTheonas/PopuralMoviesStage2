package com.udacity.popularmoviesstgone.Model;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieReviews {
    private String reviewAuthor, reviewComments, reviewUrl;

    public MovieReviews() {
    }

    public MovieReviews(String reviewAuthor, String reviewComments, String reviewUrl) {
        this.reviewAuthor = reviewAuthor;
        this.reviewComments = reviewComments;
        this.reviewUrl = reviewUrl;
    }

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
}
