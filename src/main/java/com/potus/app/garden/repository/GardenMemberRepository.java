package com.potus.app.garden.repository;

import com.potus.app.garden.model.Garden;
import com.potus.app.garden.model.GardenMember;
import com.potus.app.garden.model.Report;
import com.potus.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenMemberRepository extends JpaRepository<GardenMember, Long> {
    Optional<GardenMember> findByUser(User user);

    List<GardenMember> findByGarden(Garden garden);

}
