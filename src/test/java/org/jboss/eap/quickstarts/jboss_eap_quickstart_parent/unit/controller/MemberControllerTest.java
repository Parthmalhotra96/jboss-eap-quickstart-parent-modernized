package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import jakarta.validation.ValidationException;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    private MemberRestController memberRestController;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        memberController = new MemberController(memberRestController);
    }

    @Test
    void testShowRegistrationForm() {
        List<MemberDTO> members = List.of(new MemberDTO(1L, "John Doe", "jd@jd.com", "12351124122"));
        when(memberRestController.listAllMembers()).thenReturn(ResponseEntity.ok(members));

        String viewName = memberController.showRegistrationForm(model);

        verify(model).addAttribute(eq("newMember"), any(Member.class));
        verify(model).addAttribute("members", members);
        assertThat(viewName).isEqualTo("index");
    }

    @Test
    void testAddMember_Success() {
        Member member = new Member();
        member.setEmail("test@example.com");

        String viewName = memberController.addMember(member, redirectAttributes, bindingResult);

        verify(memberRestController).createMember(any(MemberDTO.class));
        assertThat(viewName).isEqualTo("redirect:/index");
    }

    @Test
    void testAddMember_EmailTaken() {
        Member member = new Member();
        member.setEmail("test@example.com");

        doThrow(new ValidationException("Email is already taken")).when(memberRestController).createMember(any(MemberDTO.class));

        String viewName = memberController.addMember(member, redirectAttributes, bindingResult);

        verify(redirectAttributes).addAttribute("errorMessage", "Email is already taken. Please choose a different email.");
        assertThat(viewName).isEqualTo("index");
    }

    @Test
    void testDeleteMember() {
        Long memberId = 1L;

        String viewName = memberController.deleteMember(memberId);

        verify(memberRestController).deleteMemberById(memberId);
        assertThat(viewName).isEqualTo("redirect:/index");
    }
}
