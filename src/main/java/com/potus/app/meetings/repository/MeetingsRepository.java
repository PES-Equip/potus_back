package com.potus.app.meetings.repository;

import com.potus.app.meetings.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingsRepository extends JpaRepository<Meeting, Long> {
}
