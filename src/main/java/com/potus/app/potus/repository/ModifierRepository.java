package com.potus.app.potus.repository;

import com.potus.app.potus.model.Modifier;
import com.potus.app.potus.model.ModifierEffectType;
import com.potus.app.potus.model.ModifierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier, Long> {

    List<Modifier> findByModifierType(ModifierType modifierType);
    List<Modifier> findByModifierEffectTypeAndModifierType(ModifierEffectType modifierEffectType, ModifierType modifierType);
}
