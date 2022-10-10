package com.potus.app.potus.repository;

import com.potus.app.potus.model.Potus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PotusRepository extends JpaRepository<Potus, Long> {

}
