package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.constant.LoggingConstants;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> listAllMembers() {
        log.info("Received request to list all members");

        List<Member> members = new ArrayList<>();
        try {
            members = repository.findAllByOrderByName();
        } catch (Exception exception) {
            log.error(LoggingConstants.RETRIEVAL_ERROR_CODE, LoggingConstants.RETRIEVAL_ERROR, exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        log.debug("Found {} members", members.size());
        List<MemberResponseDTO> memberDTOs = members.stream()
                .map(MemberResponseDTO::fromMember)
                .toList();
        return memberDTOs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(memberDTOs);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> lookupMemberById(@PathVariable("id") long id) {
        log.info("Received request to lookup member by id: {}", id);

        Optional<Member> member = Optional.empty();
        try {
             member = repository.findById(id);
        } catch (Exception exception) {
            log.error(LoggingConstants.RETRIEVAL_ERROR_CODE, LoggingConstants.RETRIEVAL_ERROR, exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        log.debug("Member found: {}", member.isPresent());
        return member.map(m -> ResponseEntity.ok(MemberResponseDTO.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("Received request to create new member: {}", memberRequestDTO);
        try {
            Member member = Member.fromMemberDTO(memberRequestDTO);
            validateMember(member);
            Optional<MemberResponseDTO> memberResponseDTO = registration.register(member);
            if (memberResponseDTO.isEmpty()) {
                throw new RuntimeException("Can not register Member");
            }
            log.info("Member created successfully");
            return ResponseEntity.ok(memberResponseDTO.get());
        } catch (ConstraintViolationException ce) {
            log.error(LoggingConstants.VALIDATION_FAILED_CODE, LoggingConstants.VALIDATION_FAILED, ce.getMessage());
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE, LoggingConstants.EXISTING_EMAIL_ERROR, e.getMessage());
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponseDTO> deleteMemberById(@PathVariable("id") long id) {
        log.info("Received request to delete member by id: {}", id);
        ResponseEntity<MemberResponseDTO> memberResponseDTO = this.lookupMemberById(id);

        if (!memberResponseDTO.hasBody())
            return ResponseEntity.notFound().build();

        try{
            repository.deleteById(id);
            log.info("Member deleted : {}", memberResponseDTO.getBody().name());
        }
        catch (Exception exception) {
            log.error("Error deleting member : {}", exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(memberResponseDTO.getBody());
    }

    private void validateMember(Member member) throws ValidationException {
        log.info("Validating member: {}", member);
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            log.error(LoggingConstants.VALIDATION_FAILED_CODE, LoggingConstants.VALIDATION_FAILED, violations.size());
            throw new ConstraintViolationException(violations);
        }
        if (emailAlreadyExists(member.getEmail())) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE, LoggingConstants.EXISTING_EMAIL_ERROR, member.getEmail());
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