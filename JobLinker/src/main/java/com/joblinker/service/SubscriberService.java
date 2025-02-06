package com.joblinker.service;

import com.joblinker.domain.Job;
import com.joblinker.domain.Skill;
import com.joblinker.domain.Subscriber;
import com.joblinker.domain.response.ResEmailJob;
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
    private EmailService emailService;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
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
    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Hot job opportunities are waiting for you, explore now",
                                "subscriber-email-template",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }
    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
    public void delete(Long id) {
        Subscriber existingSubscriber = this.findById(id);
        this.subscriberRepository.delete(existingSubscriber);
    }
}