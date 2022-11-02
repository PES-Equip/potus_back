package com.potus.app.garden.service;


import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.*;
import com.potus.app.garden.repository.GardenRequestRepository;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.potus.app.garden.utils.GardenExceptionMessages.REQUEST_ALREADY_EXISTS;
import static com.potus.app.garden.utils.GardenExceptionMessages.REQUEST_NOT_FOUND;
import static com.potus.app.garden.utils.GardenUtils.GARDEN_MAX_SIZE;
import static com.potus.app.garden.utils.GardenUtils.REQUEST_TIME_LIMIT;

@Service
public class GardenRequestService {

   @Autowired
   GardenRequestRepository gardenRequestRepository;

   public List<GardenRequest> findUserRequests(User user){
       return gardenRequestRepository.findByUserAndType(user, GardenRequestType.USER_INVITATION_REQUEST);
   }

   public List<GardenRequest> findAllUserRequests(User user){
       return gardenRequestRepository.findByUser(user);
   }

    public List<GardenRequest> findGardenRequests(Garden garden){
        return gardenRequestRepository.findByGardenAndType(garden, GardenRequestType.GROUP_JOIN_REQUEST);
    }

    public GardenRequest findRequest(User user, Garden garden){
        return gardenRequestRepository.findByUserAndGarden(user, garden)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND));
    }

    public boolean existsRequest(Garden garden, User user){
       return gardenRequestRepository.existsByUserAndGarden(user,garden);
    }
    private void validateRequests(List<GardenRequest> unvalidatedRequests) {

       Instant before = Instant.now().minus(Duration.ofDays(REQUEST_TIME_LIMIT));
       Date expirationDate = Date.from(before);

        List<GardenRequest> requireDeleteRequests = unvalidatedRequests.stream().filter
                (gardenRequest -> gardenRequest.getCreatedDate().before(expirationDate) ||
                                  gardenRequest.getGarden().getMembers().size() == GARDEN_MAX_SIZE
                        ).toList();

        gardenRequestRepository.deleteAll(requireDeleteRequests);
    }

    public void validateUserRequests(User user){
       validateRequests(findUserRequests(user));
    }

    public void validateGardenRequests(Garden garden){
        validateRequests(findGardenRequests(garden));
    }

    public void deleteUserRequests(User user) {
       List<GardenRequest> gardenRequests = findAllUserRequests(user);
       gardenRequestRepository.deleteAll(gardenRequests);
    }

    public void deleteRequest(GardenRequest gardenRequest) {
       gardenRequestRepository.delete(gardenRequest);
    }

    public void createRequest(User user, Garden garden, GardenRequestType type) {
        GardenRequest gardenRequest = new GardenRequest(garden,user,new Date(), type);
        try {
            gardenRequestRepository.save(gardenRequest);
        }
        catch (Exception e){
            throw new ResourceAlreadyExistsException(REQUEST_ALREADY_EXISTS);
        }
    }
}
