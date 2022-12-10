package com.potus.app.user.repository;

import com.potus.app.user.model.Trophy;
import com.potus.app.user.model.TrophyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrophyRepository extends JpaRepository<Trophy, Long> {

    Optional<Trophy> findByName(TrophyType name);

}
