package com.joblinker.repository;

import com.joblinker.domain.Skill;
import com.joblinker.domain.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber,Long>{
    boolean existsByEmail(String email);

}