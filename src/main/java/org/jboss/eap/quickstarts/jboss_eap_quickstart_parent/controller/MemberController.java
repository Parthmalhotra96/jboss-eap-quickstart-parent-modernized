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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;

import org.jboss.as.quickstarts.kitchensink.model.Member;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberRegistration memberRegistration;

    @Autowired
    public MemberController(MemberRegistration memberRegistration) {
        this.memberRegistration = memberRegistration;
    }

    @GetMapping
    @ResponseBody
    public Member getNewMember() {
        return new Member();
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody Member newMember) {
        try {
            memberRegistration.register(newMember);
            return ResponseEntity.ok("Registered!");
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }
}
