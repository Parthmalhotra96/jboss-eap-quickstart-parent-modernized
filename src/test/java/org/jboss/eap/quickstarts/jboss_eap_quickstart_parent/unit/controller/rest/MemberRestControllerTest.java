package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.ErrorResponse;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private MemberRestController controller;

    @Test
    public void testListAllMembers() {
        // Arrange
        List<Member> members = new ArrayList<>();
        members.add(new Member(1L, "John Doe", "john.doe@example.com", "1234567890"));
        when(repository.findAllByOrderByName()).thenReturn(members);

        // Act
        ResponseEntity<?> response = controller.listAllMembers();

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
        controller.lookupMemberById(1L);

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
        ResponseEntity<?> response = controller.createMember(memberRequestDTO);

        // Assert
        verify(registration, times(1)).register(any());
    }

    @Test
    public void testDeleteMemberById() {
        // Arrange
        Member member = new Member(1L, "John Doe", "john.doe@example.com", "1234567890");
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        // Act
        ResponseEntity<?> response = controller.deleteMemberById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testLookupMemberById_HappyPath() {
        // Arrange
        long memberId = 1L;
        Member member = new Member(memberId, "John Doe", "johndoe@example.com", "1234567890");
        when(repository.findById(memberId)).thenReturn(Optional.of(member));

        // Act
        ResponseEntity<?> response = controller.lookupMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MemberResponseDTO.fromMember(member), (MemberResponseDTO) response.getBody());
    }

    @Test
    void testLookupMemberById_NotFound() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = controller.lookupMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testLookupMemberById_Exception() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenThrow(new RuntimeException());

        // Act
        ResponseEntity<?> response = controller.lookupMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testLookupMemberById_ExceptionHandling() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenThrow(new RuntimeException("Test exception"));

        // Act
        ResponseEntity<?> response = controller.lookupMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error retrieving data", errorResponse.response());
    }

    @Test
    void testCreateMember_HappyPath() throws Exception {
        // Arrange
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "johndoe@example.com", "1234567890");
        Member member = Member.fromMemberDTO(memberRequestDTO);
        when(repository.save(member)).thenReturn(member);
        MemberResponseDTO memberResponseDTO = new MemberResponseDTO(100L, member.getName(), member.getEmail(), member.getPhoneNumber());
        when(registration.register(any(Member.class))).thenReturn(Optional.of(memberResponseDTO));

        // Act
        ResponseEntity<?> response = controller.createMember(memberRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(memberResponseDTO, (MemberResponseDTO) response.getBody());
    }

    @Test
    void testCreateMember_ConstraintViolationException() throws Exception {
        // Arrange
        Member member = new Member(null, null, null);
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        when(validator.validate(any())).thenThrow(new ConstraintViolationException(violations));

        MemberRequestDTO memberRequestDTO = new MemberRequestDTO(member.getName(), member.getEmail(), member.getPhoneNumber());
        // Act
        ResponseEntity<?> response = controller.createMember(memberRequestDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateMember_ValidationException() throws Exception {
        // Arrange
        Member member = new Member(null, null, null);
        when(repository.findByEmail(any())).thenReturn(Optional.of(member));

        MemberRequestDTO memberRequestDTO = new MemberRequestDTO(member.getName(), member.getEmail(), member.getPhoneNumber());
        // Act
        ResponseEntity<?> response = controller.createMember(memberRequestDTO);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void testUpdateMember_HappyPath() throws Exception {
        // Arrange
        long memberId = 1L;
        Member member = new Member(memberId, "John Doe", "johndoe@example.com", "1234567890");
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "johndoe@example.com", "1234567890");
        when(repository.findById(memberId)).thenReturn(Optional.of(member));
        when(repository.save(member)).thenReturn(member);
        MemberResponseDTO memberResponseDTO = new MemberResponseDTO(100L, member.getName(), member.getEmail(), member.getPhoneNumber());
        when(registration.update(any(), any(Member.class))).thenReturn(Optional.of(memberResponseDTO));

        // Act
        ResponseEntity<?> response = controller.updateMember(memberId, memberRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(memberResponseDTO, (MemberResponseDTO) response.getBody());
    }

    @Test
    void testUpdateMember_NotFound() throws Exception {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.empty());
        when(registration.update(any(), any(Member.class))).thenThrow(new Exception("Member not found"));

        // Act
        ResponseEntity<?> response = controller.updateMember(memberId, new MemberRequestDTO("A", "a@gmail.com", "1231231231"));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateMember_ValidationException() {
        // Arrange
        long memberId = 1L;
        Member member = new Member(memberId, null, null, null);
        when(repository.findById(memberId)).thenReturn(Optional.of(member));
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        when(repository.save(member)).thenThrow(new ConstraintViolationException(violations));

        // Act
        ResponseEntity<?> response = controller.updateMember(memberId,  new MemberRequestDTO("A", "a@gmail.com", "1231231231"));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error Updating Member", errorResponse.response());
    }

    @Test
    void testDeleteMember_HappyPath() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.of(new Member(memberId, "John Doe", "johndoe@example.com", "1234567890")));

        // Act
        ResponseEntity<?> response = controller.deleteMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteMember_NotFound() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = controller.deleteMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteMember_Exception() {
        // Arrange
        long memberId = 1L;
        when(repository.findById(memberId)).thenReturn(Optional.of(new Member(memberId, "John Doe", "johndoe@example.com", "1234567890")));
        doThrow(new RuntimeException()).when(repository).deleteById(memberId);

        // Act
        ResponseEntity<?> response = controller.deleteMemberById(memberId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
