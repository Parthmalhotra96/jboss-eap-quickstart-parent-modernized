package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.util.GenericIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GenericIdGenerator genericIdGenerator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MemberRegistration memberRegistration;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() throws Exception {
        Member member = new Member();
        member.setName("John Doe");

        when(genericIdGenerator.getNextId()).thenReturn(1L);
        when(memberRepository.save(any())).thenReturn(new Member());
        memberRegistration.register(member);

        assertEquals(1L, member.getId());
        verify(memberRepository, times(1)).save(member);
        verify(eventPublisher, times(1)).publishEvent(member);
    }

    @Test
    public void testRegisterException() {
        Member member = new Member();
        member.setName("John Doe");

        doThrow(new RuntimeException("Save failed")).when(memberRepository).save(any(Member.class));

        Exception exception = assertThrows(Exception.class, () -> {
            memberRegistration.register(member);
        });

        assertEquals("Save failed", exception.getMessage());
    }

    @Test
    public void testDeleteById() {
        Long memberId = 1L;

        memberRegistration.deleteById(memberId);

        verify(memberRepository, times(1)).deleteById(memberId);
    }
}

