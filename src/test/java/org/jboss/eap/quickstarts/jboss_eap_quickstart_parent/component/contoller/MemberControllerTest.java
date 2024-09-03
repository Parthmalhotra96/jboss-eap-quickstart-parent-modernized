package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.contoller;

import jakarta.validation.ValidationException;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRestController memberRestController;

    @Test
    void testShowRegistrationForm() throws Exception {
        List<MemberDTO> members = List.of(new MemberDTO(1L, "John Doe", "jd@jd.co", "1234432111"));
        when(memberRestController.listAllMembers()).thenReturn(ResponseEntity.ok(members));

        mockMvc.perform(get("/index"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("newMember"))
                .andExpect(MockMvcResultMatchers.model().attribute("members", members));
    }

    @Test
    void testAddMember_Success() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/index"));

        verify(memberRestController).createMember(any(MemberDTO.class));
    }

    @Test
    void testAddMember_EmailTaken() throws Exception {
        doThrow(new ValidationException("Email is already taken")).when(memberRestController).createMember(any(MemberDTO.class));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));

        verify(memberRestController).createMember(any(MemberDTO.class));
    }

    @Test
    void testDeleteMember() throws Exception {
        Long memberId = 1L;

        mockMvc.perform(post("/members/{id}/delete", memberId))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/index"));

        verify(memberRestController).deleteMemberById(memberId);
    }
}

