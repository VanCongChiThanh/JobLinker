package com.joblinker.controller;

import com.joblinker.domain.Skill;
import com.joblinker.domain.response.ResultPaginationDTO;
import com.joblinker.service.SkillService;
import com.joblinker.util.annotation.ApiMessage;
import com.joblinker.util.error.CustomException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }
    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill){
        Skill newSkill=skillService.createSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills/{id}")
    @ApiMessage("update skill")
    public ResponseEntity<Skill> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody Skill skill) {
        Skill updatedSkill = skillService.updateSkill(id, skill);
        return ResponseEntity.ok().body(updatedSkill);
    }

    @DeleteMapping("/skills/{skillID}")
    @ApiMessage("delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillID) {
        skillService.deleteSkill(skillID);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/skills/{skillID}")
    @ApiMessage("get skill by id")
    public ResponseEntity<Skill> getSkillByID(@PathVariable Long skillID){
        Skill skill = this.skillService.getSkillByID(skillID);
        return ResponseEntity.ok(skill);
    }
    @GetMapping("skills")
    @ApiMessage("get all skills")
    public ResponseEntity<ResultPaginationDTO> getSkills(
            @Filter Specification<Skill> spec,
            Pageable pageable
            )
    {
        return ResponseEntity.ok(skillService.getAllSkills(spec, pageable));
    }
}