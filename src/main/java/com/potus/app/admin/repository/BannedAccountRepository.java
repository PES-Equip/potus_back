package com.potus.app.admin.repository;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.model.BannedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BannedAccountRepository extends JpaRepository<BannedAccount, Long> {

    boolean existsByEmail(String email);

    BannedAccount findByEmail(String email);
}
