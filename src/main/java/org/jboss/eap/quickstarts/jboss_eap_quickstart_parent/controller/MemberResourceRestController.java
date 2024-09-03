/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/members")
@Validated
public class MemberResourceRestController {

    @Autowired
    private Validator validator;

    @Autowired
    private MemberRepository repository;

    @Autowired
    private MemberRegistration registration;

    @GetMapping
    public List<Member> listAllMembers() {
        return repository.findAllByIdOrderedByName();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {
        Optional<Member> member = repository.findById(id);
        return member.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        try {
            validateMember(member);
            registration.register(member);
            return ResponseEntity.ok().build();
        } catch (ConstraintViolationException ce) {
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.status(409).body(responseObj);
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }

    private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if (emailAlreadyExists(member.getEmail())) {
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
        Optional<Member> member = repository.findByEmail(email);
        return member.isPresent();
    }
}

