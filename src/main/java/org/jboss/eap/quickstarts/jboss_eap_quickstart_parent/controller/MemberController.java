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
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberRegistration memberRegistration;
    private final MemberRestController memberRestController;

    @GetMapping("/index")
    public String showRegistrationForm(Model model) {
        model.addAttribute("newMember", new Member());
        List<Member> members = memberRestController.listAllMembers().getBody();
        model.addAttribute("members", members);

        members.forEach(member -> System.out.println("Member: " + member.getId() + ", " + member.getName()));

        return "index";
    }

    @PostMapping("/members")
    public String addMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        try {
            memberRestController.createMember(member);
        } catch (ValidationException e) {
            redirectAttributes.addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
            return "index";
        } catch (Exception e) {
            redirectAttributes.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "index";
        }
        return "redirect:/index";
    }

//    @PostMapping("/members/{id}/delete")
//    public String deleteMember(@PathVariable Long id) {
//        memberRestController.deleteMember(id);
//        return "redirect:/index";
//    }
}
