package com.potus.app.admin.repository;

import com.potus.app.admin.model.BanRequest;
import com.potus.app.admin.model.BannedAccount;
import com.potus.app.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BanRequestRepository extends JpaRepository<BanRequest, Long> {

    BanRequest findByUser(User sender);
}
