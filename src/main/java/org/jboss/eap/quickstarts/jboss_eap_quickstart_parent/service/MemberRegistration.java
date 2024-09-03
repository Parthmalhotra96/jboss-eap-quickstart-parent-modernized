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
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MemberRegistration {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void register(Member member) throws Exception {
        log.info("Registering {}", member.getName());
        member.setId(idGenerator.getNextId());
        memberRepository.save(member);
        eventPublisher.publishEvent(member);
    }

    public void deleteById(Long id) {
        log.info("Deleting member with " + id);
        memberRepository.deleteById(id);
    }

    public List<Member> getAllMembersByName() {
        log.info("Fetching all the members ");
        return memberRepository.findAllByOrder(Sort.by(Sort.Order.asc("name")));
    }


}
