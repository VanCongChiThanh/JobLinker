package com.joblinker.service;

import com.joblinker.domain.Skill;
import com.joblinker.domain.response.ResCreateUserDTO;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.repository.JobRepository;
import com.joblinker.repository.SkillRepository;
import com.joblinker.util.error.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.JobStateReason;
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
        if (!skillRepository.existsById(id)) {
            throw new CustomException("Skill with id = " + id + "not found");
        }
        skillRepository.deleteById(id);
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
    public Skill updateSkill(Skill skill) {
        Skill currentSkill = skillRepository.findById(skill.getId())
                .orElseThrow(() -> new CustomException("Skill id = " + skill.getId() + "is not exist"));

        if (skill.getName() != null && isNameExist(skill.getName())) {
            throw new CustomException("Skill name = " + skill.getName() + " is existing");
        }

        currentSkill.setName(skill.getName());
        return skillRepository.save(currentSkill);
    }


}
