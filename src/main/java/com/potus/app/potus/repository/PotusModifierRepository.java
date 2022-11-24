package com.potus.app.potus.repository;

import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.Potus;
import com.potus.app.potus.model.PotusModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PotusModifierRepository extends JpaRepository<PotusModifier, Long> {

    List<PotusModifier> findByPotus(Potus potus);
}
