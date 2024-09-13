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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.constant.LoggingConstants;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/members")
public class MemberController {

    @Value("${ui.v2.enabled}")
    private boolean v2Enabled;

    private final MemberRestController memberRestController;

    @Operation(summary = "Get all members", description = "Returns a list of all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members found", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/html"))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("newMember", new Member());
        ResponseEntity<?> memberList = memberRestController.listAllMembers();
        List<MemberResponseDTO> members = new ArrayList<>();

        if (memberList.getBody() instanceof List<?>) {
            members = (List<MemberResponseDTO>) memberList.getBody();
            members = members.stream().sorted().toList();
        }
        model.addAttribute("members", members);

        Optional.of(members)
                .ifPresent(m -> m.forEach(member -> log.debug("Member: {}, {}", member.id(), member.name())));

        return v2Enabled? "indexv2" : "index";
    }

    @GetMapping("/default")
    public String defaultPage(){
        return "default";
    }

    @Operation(summary = "Create a new member", description = "Creates a new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member created", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "409", description = "Email already taken", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/html"))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping
    public String addMember(@ModelAttribute("newMember") MemberRequestDTO member, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        log.debug("Adding new member: {}", member);
        try {
            memberRestController.createMember(member);
        } catch (ValidationException e) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE + " : " + LoggingConstants.EXISTING_EMAIL_ERROR, e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
            return v2Enabled? "indexv2" : "index";
        } catch (Exception e) {
            log.error(LoggingConstants.UNEXPECTED_ERROR_CODE + " : " +  LoggingConstants.UNEXPECTED_ERROR, e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return v2Enabled? "indexv2" : "index";
        }
        log.debug("Member added successfully");
        return "redirect:/members";
    }

    @Operation(summary = "Update a member", description = "Updates a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/html"))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/{id}/update")
    public String updateMember(@Parameter(
            description = "ID of member to be updated",
            required = true) @PathVariable Long id, @ModelAttribute("newMember") MemberRequestDTO member, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        log.debug("Updating member with ID: {}", id);
        try {
            memberRestController.updateMember(id, member);
        } catch (ValidationException e) {
            log.error(LoggingConstants.EXISTING_EMAIL_ERROR_CODE+ " : " + LoggingConstants.EXISTING_EMAIL_ERROR, e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
            return v2Enabled? "updatev2" : "update";
        } catch (Exception e) {
            log.error(LoggingConstants.UNEXPECTED_ERROR_CODE + " : " + LoggingConstants.UNEXPECTED_ERROR, e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return v2Enabled? "updatev2" : "update";
        }
        log.debug("Member updated successfully");
        return "redirect:/members";
    }

    @Operation(summary = "Delete a member", description = "Deletes a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member deleted", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/html"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteMember(@Parameter(
            description = "ID of member to be deleted",
            required = true) @PathVariable Long id) {
        log.debug("Deleting member with ID: {}", id);
        memberRestController.deleteMemberById(id);
        log.debug("Member deleted successfully");
        return "redirect:/members";
    }

    @Operation(summary = "Get member update form", description = "Returns the update form for a member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member update form found", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "text/html"))
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}/update")
    public String updateMemberForm(@PathVariable Long id, Model model) {
        ResponseEntity<?> member = memberRestController.lookupMemberById(id);
        if (member.hasBody()) {
            model.addAttribute("member", member.getBody());
            return v2Enabled? "updatev2" : "update";
        }
        return v2Enabled? "indexv2" : "index";
    }
}