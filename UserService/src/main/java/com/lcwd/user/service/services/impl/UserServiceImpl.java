package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelServices;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserServices {

    private static final String URL_API_USER_ID = "http://RATING-SERVICE/ratings/users/";
    private static final String URL_API_HOTEL_ID = "http://HOTEL-SERVICE/hotels/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelServices hotelServices;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {

        List<User> allUsers = userRepository.findAll();

        for(User userId: allUsers){
//            ArrayList<Rating> ratingOfUser = restTemplate.getForObject(URL_API_USER_ID+userId.getUserId(), ArrayList.class);
//            logger.info("user ID: {} ratingOfUser: {}",userId.getUserId(), ratingOfUser);
//            userId.setRatings(ratingOfUser);
            Rating[] ratingOfUser = restTemplate.getForObject(URL_API_USER_ID +userId.getUserId(), Rating[].class);
            logger.info("user ID: {} ratingOfUser: {}",userId.getUserId(), ratingOfUser);

            List<Rating> ratings = Arrays.asList(ratingOfUser);

            List<Rating> ratingList = ratings.stream().map(rating -> {
                //API call to hoel service to get the hotel
                //http://localhost:8082/hotels/ac7f4587-8f16-4209-b365-0ac0dd26dbac
                ResponseEntity<Hotel> forEntity = restTemplate.getForEntity(URL_API_HOTEL_ID+rating.getHotelId(), Hotel.class);
                //Hotel hotel = forEntity.getBody();
                Hotel hotel = hotelServices.getHotel(rating.getHotelId());
                logger.info("Response Status code: {}",forEntity.getStatusCode());

                // set the hotel to rating
                rating.setHotel(hotel);
                //return the rating
                return rating;
            }).collect(Collectors.toList());

            userId.setRatings(ratingList);

        }
        return allUsers;
    }

    @Override
    public User getUser(String userId) {
        // get user form db with the help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with give id is not found on server !! : "+userId));
        // fetch rating of the above user from rating service
        //http://localhost:8083/ratings/users/10ab911d-1720-4590-95f6-3fa8f906f6f1

        Rating[] ratingOfUser = restTemplate.getForObject(URL_API_USER_ID +user.getUserId(), Rating[].class);
        logger.info("ratingOfUser: {}", ratingOfUser);

        List<Rating> ratings = Arrays.asList(ratingOfUser);

        List<Rating> ratingList = ratings.stream().map(rating -> {
            //API call to hoel service to get the hotel
            //http://localhost:8082/hotels/ac7f4587-8f16-4209-b365-0ac0dd26dbac
            //ResponseEntity<Hotel> forEntity = restTemplate.getForEntity(URL_API_HOTEL_ID+rating.getHotelId(), Hotel.class);

//            Hotel hotel = forEntity.getBody();
            Hotel hotel = hotelServices.getHotel(rating.getHotelId());
            //logger.info("Response Status code: {}",forEntity.getStatusCode());

            // set the hotel to rating
            rating.setHotel(hotel);
            //return the rating
            return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return user;
    }
}
