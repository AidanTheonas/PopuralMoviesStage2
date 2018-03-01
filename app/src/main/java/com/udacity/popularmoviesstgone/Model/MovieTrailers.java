package com.udacity.popularmoviesstgone.Model;

/**
 * PopuralMoviesStage2 Created by aidan on 01/03/2018.
 */

public class MovieTrailers {
    private String trailerID, trailerDescription;

    public MovieTrailers() {
    }

    public MovieTrailers(String trailerID, String trailerDescription) {
        this.trailerID = trailerID;
        this.trailerDescription = trailerDescription;
    }

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
}
