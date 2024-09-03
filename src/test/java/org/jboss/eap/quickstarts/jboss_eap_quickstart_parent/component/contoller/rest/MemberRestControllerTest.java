package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.contoller.rest;

import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(MemberRestController.class)
public class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository repository;

    @MockBean
    private MemberRegistration registration;

    @MockBean
    private Validator validator;

    @Test
    void testListAllMembers() throws Exception {
        List<Member> members = List.of(new Member(1L, "John Doe", "john@example.com", "1121131313"));
        when(repository.findAllByOrderByName()).thenReturn(members);

        mockMvc.perform(get("/rest/members"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(members.size()));
    }

    @Test
    void testLookupMemberById_Found() throws Exception {
        Member member = new Member(1L, "John Doe", "john@example.com", "1234432112");
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        mockMvc.perform(get("/rest/members/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(member.getName()));
    }

    @Test
    void testLookupMemberById_NotFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/members/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testCreateMember_Success() throws Exception {
        MemberDTO memberDTO = new MemberDTO(1L, "John Doe", "john@example.com", "9900886655");
        Member member = Member.fromMemberDTO(memberDTO);

        mockMvc.perform(post("/rest/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(registration).register(any(Member.class));
    }

    @Test
    void testCreateMember_EmailTaken() throws Exception {
        MemberDTO memberDTO = new MemberDTO(1L, "John Doe", "john@example.com", "12313131231");
        Member member = Member.fromMemberDTO(memberDTO);
        doThrow(new ValidationException("Unique Email Violation")).when(registration).register(any(Member.class));

        mockMvc.perform(post("/rest/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Email taken"));
    }

    @Test
    void testDeleteMemberById_Success() throws Exception {
        Member member = new Member(1L, "John Doe", "john@example.com", "12313123131");
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        mockMvc.perform(delete("/rest/members/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteMemberById_NotFound() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/rest/members/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

