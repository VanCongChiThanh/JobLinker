package com.joblinker.service;

import com.joblinker.domain.Skill;
import com.joblinker.domain.Subscriber;
import com.joblinker.repository.JobRepository;
import com.joblinker.repository.SkillRepository;
import com.joblinker.repository.SubscriberRepository;
import com.joblinker.util.error.IdInvalidException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }
    public Subscriber findById(Long id) {
        return subscriberRepository.findById(id)
                .orElseThrow(()->new IdInvalidException("Subscriber does not exist"));
    }
    public boolean isEmailExist(String email){
        return subscriberRepository.existsByEmail(email);
    }
    public Subscriber create(Subscriber subscriber){
        boolean isExist = this.isEmailExist(subscriber.getEmail());
        if (isExist == true) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        }
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return subscriberRepository.save(subscriber);
    }
    public Subscriber update(Long id, Subscriber subscriber){
        Subscriber existingSubscriber = this.findById(id);
        if(subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills()
                   .stream().map(x -> x.getId())
                   .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            existingSubscriber.setSkills(dbSkills);
        }
        existingSubscriber.setEmail(subscriber.getEmail());
        return subscriberRepository.save(existingSubscriber);
    }
}