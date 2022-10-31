package com.potus.app.potus.repository;

import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusRegistry;
import com.potus.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PotusRegistryRepository extends JpaRepository<PotusRegistry, Long> {

    List<PotusRegistry> findByUser(User user);

    boolean existsByUserAndName(User user, String name);

}
