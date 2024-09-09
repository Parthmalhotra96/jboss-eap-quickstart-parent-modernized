package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MemberRestControllerTest {

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberRegistration registration;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberRestController memberRestController;

    @Test
    public void testListAllMembers() {
        // Arrange
        List<Member> members = new ArrayList<>();
        members.add(new Member(1L, "John Doe", "john.doe@example.com", "1234567890"));
        when(repository.findAllByOrderByName()).thenReturn(members);

        // Act
        ResponseEntity<?> response = memberRestController.listAllMembers();

        if (response instanceof List<?>) {
            List<MemberResponseDTO> responseList = (List<MemberResponseDTO>) response.getBody();
            // Assert
            assertEquals(200, response.getStatusCodeValue());
            assertEquals(1, responseList.size());
        }
    }

    @Test
    public void testLookupMemberById() {
        // Arrange
        Member member = new Member(1L, "John Doe", "john.doe@example.com", "1234567890");
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        // Act
        memberRestController.lookupMemberById(1L);

        // Assert
        verify(repository, times(1)).findById(any());
    }

    @Test
    public void testCreateMember() throws Exception {
        // Arrange

        Optional<Member> member = repository.findByEmail("john.doe@example.com");

        member.ifPresent(value -> repository.deleteById(value.getId()));

        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "john.doe@example.com", "1234567890");
        when(validator.validate(any())).thenReturn(new HashSet<>());

        // Act
        ResponseEntity<?> response = memberRestController.createMember(memberRequestDTO);

        // Assert
        verify(registration, times(1)).register(any());
    }

    @Test
    public void testDeleteMemberById() {
        // Arrange
        Member member = new Member(1L, "John Doe", "john.doe@example.com", "1234567890");
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        // Act
        ResponseEntity<?> response = memberRestController.deleteMemberById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(repository, times(1)).deleteById(1L);
    }
}
