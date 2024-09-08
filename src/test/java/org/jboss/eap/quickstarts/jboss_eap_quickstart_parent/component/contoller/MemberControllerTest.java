package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.contoller;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import java.awt.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRestController memberRestController; // Mocking the dependency

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testShowRegistrationForm() throws Exception {

        when(memberRestController.listAllMembers()).thenReturn(ResponseEntity.of(Optional.of(List.of(new MemberResponseDTO(1L, "John Doe", "john.doe@example.com", "1234567890")))));
        mockMvc.perform(MockMvcRequestBuilders.get("/members"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAddMember() throws Exception {
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "john.doe@example.com", "1234567890");
        when(memberRestController.createMember(any(MemberRequestDTO.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(MockMvcRequestBuilders.post("/members")
                        .param("name", memberRequestDTO.name())
                        .param("email", memberRequestDTO.email())
                        .param("phoneNumber", memberRequestDTO.phoneNumber()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteMember() throws Exception {
        when(memberRestController.deleteMemberById(any(Long.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(MockMvcRequestBuilders.post("/members/1/delete"))
                .andDo(print());
    }
}

