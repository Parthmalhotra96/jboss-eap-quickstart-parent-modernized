package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.contoller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberResponseDTO;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.service.MemberRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@MockitoSettings(strictness = Strictness.LENIENT)
public class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberRegistration memberRegistration;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testListAllMembers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/members"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testLookupMemberById() throws Exception {
        memberRepository.deleteAll();
        memberRegistration.register(Member.fromMemberDTO(new MemberRequestDTO("Jane Doe", "jane.doe@example.com", "1234123112")));
        List<Member> members = memberRepository.findAllByOrderByName();

        Long id = members.getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/members/"+id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateMember() throws Exception {
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("Jane Doe", "jane.doe2@example.com", "1234123112");
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(memberRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteMemberById() throws Exception {
        memberRepository.deleteAll();
        memberRegistration.register(Member.fromMemberDTO(new MemberRequestDTO("Jane Doe", "jane.doe@example.com", "1234123112")));
        List<Member> members = memberRepository.findAllByOrderByName();

        Long id = members.getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/rest/members/"+id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}