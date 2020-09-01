package com.jlisok.youtube_activity_manager.userPersonalData.repositories;

import com.jlisok.youtube_activity_manager.userPersonalData.models.UserPersonalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPersonalDataRepository extends JpaRepository<UserPersonalData, UUID> {

}
