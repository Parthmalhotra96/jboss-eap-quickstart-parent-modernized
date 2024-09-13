package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.contoller;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MemberController.class)
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRestController memberRestController; // Mocking the dependency

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testShowRegistrationForm() throws Exception {

        doReturn(ResponseEntity.of(Optional.of(List.of(new MemberResponseDTO(1L, "John Doe", "john.doe@example.com", "1234567890"))))).when(memberRestController).listAllMembers();
        mockMvc.perform(MockMvcRequestBuilders.get("/members"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAddMember() throws Exception {
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "john.doe@example.com", "1234567890");
        when(memberRestController.createMember(any(MemberRequestDTO.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/members")
                        .param("name", memberRequestDTO.name())
                        .param("email", memberRequestDTO.email())
                        .param("phoneNumber", memberRequestDTO.phoneNumber()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteMember() throws Exception {
        when(memberRestController.deleteMemberById(any(Long.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/members/1/delete"))
                .andDo(print());
    }
}

