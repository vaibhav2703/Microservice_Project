package com.lcwd.ratings.services;

import com.lcwd.ratings.entities.Rating;

import java.util.List;

public interface RatingService {
    //create
    Rating create(Rating rating);

    //get all ratings
    List<Rating> getRatings();

    //get all by userId
    List<Rating> getRatingsByUserId(String userId);

    //get all by hotel
    List<Rating> getRatingByHotelId(String hotelId);
}
