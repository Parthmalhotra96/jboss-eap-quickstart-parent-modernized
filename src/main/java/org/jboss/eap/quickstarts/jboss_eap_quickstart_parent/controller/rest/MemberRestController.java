package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/rest/members")
public class MemberRestController {

    @Autowired
    private Validator validator;

    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberRegistration registration;

    @GetMapping
    public ResponseEntity<List<MemberDTO>> listAllMembers() {
        log.info("Received request to list all members");
        List<Member> members = repository.findAllByOrderByName();
        log.debug("Found {} members", members.size());
        List<MemberDTO> memberDTOs = members.stream()
                .map(MemberDTO::fromMember)
                .toList();
        return memberDTOs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(memberDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> lookupMemberById(@PathVariable("id") long id) {
        log.info("Received request to lookup member by id: {}", id);
        Optional<Member> member = repository.findById(id);
        log.debug("Member found: {}", member.isPresent());
        return member.map(m -> ResponseEntity.ok(MemberDTO.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberDTO memberDTO) {
        log.info("Received request to create new member: {}", memberDTO);
        try {
            Member member = Member.fromMemberDTO(memberDTO);
            validateMember(member);
            registration.register(member);
            log.info("Member created successfully");
            return ResponseEntity.ok().build();
        } catch (ConstraintViolationException ce) {
            log.error("Validation failed: {}", ce.getMessage());
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.error("Email already exists: {}", e.getMessage());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(409).body(responseObj);
        } catch (Exception e) {
            log.error("Error creating member: {}", e.getMessage());
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberDTO> deleteMemberById(@PathVariable("id") long id) {
        log.info("Received request to delete member by id: {}", id);
        ResponseEntity<MemberDTO> memberDTO = this.lookupMemberById(id);

        if (!memberDTO.hasBody())
            return ResponseEntity.notFound().build();

        try{
            repository.deleteById(id);
            log.info("Member deleted : {}", memberDTO.getBody().name());
        }
        catch (Exception exception) {
            log.error("Error deleting member : {}", exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(memberDTO.getBody());
    }

    private void validateMember(Member member) throws ValidationException {
        log.info("Validating member: {}", member);
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            log.error("Validation failed: {}", violations.size());
            throw new ConstraintViolationException(violations);
        }
        if (emailAlreadyExists(member.getEmail())) {
            log.error("Email already exists: {}", member.getEmail());
            throw new ValidationException("Unique Email Violation");
        }
    }

    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.error("Validation completed. Violations found: {}", violations.size());
        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity.badRequest().body(responseObj);
    }

    public boolean emailAlreadyExists(String email) {
        log.info("Checking if email already exists: {}", email);
        Optional<Member> member = repository.findByEmail(email);
        log.debug("Email exists: {}", member.isPresent());
        return member.isPresent();
    }
}