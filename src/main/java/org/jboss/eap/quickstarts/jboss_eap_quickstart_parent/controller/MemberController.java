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

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberRestController memberRestController;

    @GetMapping("/index")
    public String showRegistrationForm(Model model) {
        model.addAttribute("newMember", new Member());
        List<MemberDTO> members = memberRestController.listAllMembers().getBody();
        model.addAttribute("members", members);

        Optional.ofNullable(members)
                .ifPresent(m -> m.forEach(member -> log.debug("Member: {}, {}", member.id(), member.name())));

        return "index";
    }

    @PostMapping("/members")
    public String addMember(@ModelAttribute("newMember") Member member, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        log.debug("Adding new member: {}", member);
        try {
            memberRestController.createMember(MemberDTO.fromMember(member));
        } catch (ValidationException e) {
            log.error("Email is already taken: {}", e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
            return "index";
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            redirectAttributes.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "index";
        }
        log.debug("Member added successfully");
        return "redirect:/index";
    }

    //TODO: replace the delete in endpoint with delete mapping
    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id) {
        log.debug("Deleting member with ID: {}", id);
        memberRestController.deleteMemberById(id);
        log.debug("Member deleted successfully");
        return "redirect:/index";
    }
}