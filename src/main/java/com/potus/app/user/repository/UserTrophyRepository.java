package com.potus.app.user.repository;

import com.potus.app.user.model.Trophy;
import com.potus.app.user.model.TrophyType;
import com.potus.app.user.model.User;
import com.potus.app.user.model.UserTrophy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTrophyRepository extends JpaRepository<UserTrophy, Long> {

    List<UserTrophy> findByUser(TrophyType type);

    Optional<UserTrophy> findByUserAndTrophy(User user, Trophy trophy);

}
