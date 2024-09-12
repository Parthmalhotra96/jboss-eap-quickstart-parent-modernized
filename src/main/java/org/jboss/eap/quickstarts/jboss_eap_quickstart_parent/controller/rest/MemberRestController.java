package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.constant.ErrorConstants;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.constant.LoggingConstants;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.ErrorResponse;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/rest/members")
@Tag(name = "REST", description = "REST APIs")
public class MemberRestController {

    @Autowired
    private Validator validator;

    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberRegistration registration;

    @Operation(summary = "Get all members", description = "Returns a list of all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members found", content = { @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MemberResponseDTO.class))) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public ResponseEntity<?> listAllMembers() {
        log.info("Received request to list all members");

        List<Member> members = new ArrayList<>();
        try {
            members = repository.findAllByOrderByName();
        } catch (Exception exception) {
            log.error(LoggingConstants.RETRIEVAL_ERROR_CODE + " : " + LoggingConstants.RETRIEVAL_ERROR, exception.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse(ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_CODE, ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_DESC));
        }

        log.debug("Found {} members", members.size());
        List<MemberResponseDTO> memberDTOs = members.stream()
                .map(MemberResponseDTO::fromMember)
                .toList();
        return memberDTOs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(memberDTOs);
    }

    @Operation(summary = "Get member by ID", description = "Returns a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Member not found", content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> lookupMemberById(@Parameter(
            description = "ID of member to be retrieved",
            required = true) @PathVariable("id") long id) {
        log.info("Received request to lookup member by id: {}", id);

        Optional<Member> member = Optional.empty();
        try {
             member = repository.findById(id);
        } catch (Exception exception) {
            log.error(LoggingConstants.RETRIEVAL_ERROR_CODE + " : " + LoggingConstants.RETRIEVAL_ERROR, exception.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse(ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_CODE, ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_DESC));
        }

        log.debug("Member found: {}", member.isPresent());
        return member.map(m -> ResponseEntity.ok(MemberResponseDTO.fromMember(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new member", description = "Creates a new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "409", description = "Email already taken", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) })
    })
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
            log.error(LoggingConstants.VALIDATION_FAILED_CODE + " : " + LoggingConstants.VALIDATION_FAILED, ce.getMessage());
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE + " : " + LoggingConstants.EXISTING_EMAIL_ERROR, e.getMessage());
            return ResponseEntity.status(409).body(new ErrorResponse(ErrorConstants.ERROR_EMAIL_ALREADY_TAKEN_CODE, ErrorConstants.ERROR_EMAIL_ALREADY_TAKEN_DESC));
        } catch (Exception e) {
            log.error("Error creating member: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorConstants.ERROR_CREATING_MEMBER_CODE, ErrorConstants.ERROR_CREATING_MEMBER_DESC));
        }
    }

    @Operation(summary = "Update a member", description = "Updates a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Member not found", content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@Parameter(
            description = "ID of member to be updated",
            required = true) @PathVariable("id") long id, @Valid @RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("Received request to update member by id: {}", id);
        try {
            Member member = Member.fromMemberDTO(memberRequestDTO);
            Optional<MemberResponseDTO> memberResponseDTO = registration.update(id, member);
            if (memberResponseDTO.isEmpty()) {
                throw new RuntimeException("Can not update Member");
            }
            log.info("Member updated successfully");
            return ResponseEntity.ok(memberResponseDTO.get());
        } catch (ConstraintViolationException ce) {
            log.error(LoggingConstants.VALIDATION_FAILED_CODE + " : " + LoggingConstants.VALIDATION_FAILED, ce.getMessage());
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE + " : " + LoggingConstants.EXISTING_EMAIL_ERROR, e.getMessage());
            return ResponseEntity.status(409).body(new ErrorResponse(ErrorConstants.ERROR_EMAIL_ALREADY_TAKEN_CODE, ErrorConstants.ERROR_EMAIL_ALREADY_TAKEN_DESC));
        } catch (Exception e) {
            log.error("Error updating member: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(ErrorConstants.ERROR_UPDATING_MEMBER_CODE, ErrorConstants.ERROR_UPDATING_MEMBER_DESC));
        }
    }

    @Operation(summary = "Delete a member", description = "Deletes a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member deleted", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Member not found", content = { @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMemberById(@Parameter(
            description = "ID of member to be deleted",
            required = true) @PathVariable("id") long id) {
        log.info("Received request to delete member by id: {}", id);
        ResponseEntity<?> memberResponseDTO = this.lookupMemberById(id);

        if (!memberResponseDTO.hasBody())
            return ResponseEntity.notFound().build();

        try{
            repository.deleteById(id);
            log.info("Member deleted : {}", ((MemberResponseDTO)memberResponseDTO.getBody()).name());
        }
        catch (Exception exception) {
            log.error(LoggingConstants.DELETING_ERROR_CODE + " : " + LoggingConstants.DELETING_ERROR, exception.getMessage());
            return ResponseEntity.internalServerError().body(new ErrorResponse(ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_CODE, ErrorConstants.ERROR_RETRIEVING_DATA_ERROR_DESC));
        }

        return ResponseEntity.ok(memberResponseDTO.getBody());
    }

    private void validateMember(Member member) throws ValidationException {
        log.info("Validating member: {}", member);
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            log.error(LoggingConstants.VALIDATION_FAILED_CODE + " : " + LoggingConstants.VALIDATION_FAILED, violations.size());
            throw new ConstraintViolationException(violations);
        }
        if (emailAlreadyExists(member.getEmail())) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE + " : " + LoggingConstants.EXISTING_EMAIL_ERROR, member.getEmail());
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