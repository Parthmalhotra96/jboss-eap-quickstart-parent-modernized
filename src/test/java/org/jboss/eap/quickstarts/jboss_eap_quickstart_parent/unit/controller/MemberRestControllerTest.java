package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class MemberRestControllerTest {

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberRegistration registration;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberRestController controller;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        MockitoAnnotations.initMocks(this);    }

    @Test
    public void testListAllMembers() {
        when(repository.findAllByOrderByName()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Member>> response = controller.listAllMembers();
        assertEquals(ResponseEntity.noContent().build(), response);
    }

    @Test
    public void testLookupMemberById() {
        Member member = new Member();
        member.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        ResponseEntity<Member> response = controller.lookupMemberById(1L);
        assertEquals(ResponseEntity.ok(member), response);
    }

    @Test
    public void testCreateMember() {
        Member member = new Member();
        member.setEmail("test@example.com");

        when(validator.validate(any(Member.class))).thenReturn(Collections.emptySet());
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.createMember(member);
        assertEquals(ResponseEntity.ok().build(), response);
    }
}
