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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MemberRegistrationTest {

    @Autowired
    private MemberRegistration memberRegistration;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private GenericIdGenerator genericIdGenerator;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void testRegister() throws Exception {
        Member member = new Member( "John Doe", "asfafadf.asda@gmai.com", "21313123123");

        when(genericIdGenerator.getNextId()).thenReturn(1L);

        memberRegistration.register(member);

        verify(genericIdGenerator).getNextId();
        verify(memberRepository).save(member);
    }

    @Test
    public void testDeleteById() {
        Long memberId = 1L;

        memberRegistration.deleteById(memberId);

        verify(memberRepository).deleteById(memberId);
    }
}
