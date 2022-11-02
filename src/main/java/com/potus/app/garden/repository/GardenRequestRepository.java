package com.potus.app.garden.repository;

import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenRequest;
import com.potus.app.garden.model.GardenRequestType;
import com.potus.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GardenRequestRepository extends JpaRepository<GardenRequest, Long> {


    List<GardenRequest> findByUser(User user);

    List<GardenRequest> findByGarden(Garden garden);

    List<GardenRequest> findByGardenAndType(Garden garden, GardenRequestType type);

    List<GardenRequest> findByUserAndType(User user, GardenRequestType type);

    Optional<GardenRequest> findByUserAndGarden(User user, Garden garden);

    boolean existsByUserAndGarden(User user, Garden garden);
}
