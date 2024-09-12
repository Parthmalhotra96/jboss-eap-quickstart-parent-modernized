package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.service;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util.GenericIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MemberRegistrationTest {

    @Autowired
    private MemberRegistration memberRegistration;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private GenericIdGenerator genericIdGenerator;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void testRegister() throws Exception {
        memberRepository.deleteAll();
        Member member = new Member( 100L, "John Doe", "asfafadf.asda@gmai.com", "21313123123");
        memberRegistration.register(member);

        List<Member> members = memberRepository.findAll();
        assertFalse(members.isEmpty());
    }

    @Test
    public void testDeleteById() {
        Long memberId = 100L;

        memberRegistration.deleteById(memberId);

        Optional<Member> member = memberRepository.findById(memberId);
        assertTrue(member.isEmpty());
    }
}
