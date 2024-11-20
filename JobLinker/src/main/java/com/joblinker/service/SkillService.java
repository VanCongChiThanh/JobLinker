package com.joblinker.service;

import com.joblinker.domain.Skill;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.JobRepository;
import com.joblinker.repository.SkillRepository;
import com.joblinker.util.error.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    public SkillService(SkillRepository skillRepository, JobRepository jobRepository) {
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }
    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }
    public void deleteSkill(Long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if(!skillOptional.isPresent()) {
            throw new CustomException("Skill with id = " + id + " is not found");
        }
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);

    }

    public ResultPaginationDTO getAllSkills(Specification<Skill> spec, Pageable pageable){
        Page<Skill> pSkill=this.skillRepository.findAll(spec,pageable);
        ResultPaginationDTO rs=new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt=new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pSkill.getTotalPages());
        mt.setTotal(pSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pSkill.getContent());
        return rs;
    }
    public Skill getSkillByID(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new CustomException("Skill with id = " + id + " is not found"));
    }

    public boolean isNameExist(String name){
        return skillRepository.existsByName(name);
    }
    public Skill updateSkill(Long id, Skill skill) {
        Skill currentSkill = skillRepository.findById(id)
                .orElseThrow(() -> new CustomException("Skill id = " + id + " is not exist"));

        if (skill.getName() != null && isNameExist(skill.getName())) {
            throw new CustomException("Skill name = " + skill.getName() + " is existing");
        }

        currentSkill.setName(skill.getName());
        return skillRepository.save(currentSkill);
    }

}