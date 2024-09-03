package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @InjectMocks
    private MemberController controller;

    @Mock
    private MemberRegistration memberRegistration;

    @Before
    public void setup() {
        // Initialize the mocks
        openMocks(this);
    }

    @Test
    public void testGetNewMember() {
        // When
        Member member = controller.getNewMember();

        // Then
        assertNotNull(member);
    }

    @Test
    public void testRegister_Valid() throws Exception {
        // Given
        Member newMember = new Member();

        // When
        ResponseEntity<String> response = controller.register(newMember);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registered!", response.getBody());
    }

    @Test
    public void testRegister_Invalid() throws Exception {
        // Given
        Member newMember = new Member();
        doThrow(new RuntimeException("Registration failed")).when(memberRegistration).register(any(Member.class));

        // When
        ResponseEntity<String> response = controller.register(newMember);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed. See server log for more information", response.getBody());
    }
}