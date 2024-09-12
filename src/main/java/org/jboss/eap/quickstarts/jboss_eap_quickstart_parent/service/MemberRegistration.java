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
package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service;

import lombok.extern.slf4j.Slf4j;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util.GenericIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberRegistration {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GenericIdGenerator genericIdGenerator;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Optional<MemberResponseDTO> register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        member.setId(genericIdGenerator.getNextId());
        MemberResponseDTO memberResponseDTO = MemberResponseDTO.fromMember(memberRepository.save(member));
        eventPublisher.publishEvent(member);

        return Optional.of(memberResponseDTO);
    }

    public Optional<MemberResponseDTO> update(Long id, Member member) throws Exception {
        log.info("Updating member with ID: {}", id);
        Optional<Member> existingMember = memberRepository.findById(id);
        if (existingMember.isPresent()) {
            member.setId(id);
            Member updatedMember = memberRepository.save(member);
            return Optional.of(MemberResponseDTO.fromMember(updatedMember));
        } else {
            log.error("Member not found with ID: {}", id);
            throw new Exception("Member not found");
        }
    }

    public void deleteById(Long id) {
        log.info("Deleting member with {}", id);
        memberRepository.deleteById(id);
    }
}
