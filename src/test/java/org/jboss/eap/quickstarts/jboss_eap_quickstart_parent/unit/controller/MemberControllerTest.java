package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.controller;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.MemberController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.controller.rest.MemberRestController;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto.MemberRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    private MemberRestController memberRestController;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private MemberController memberController;

    @Test
    public void testShowRegistrationForm() {
        when(memberRestController.listAllMembers()).thenReturn(ResponseEntity.ok().build());

        // Act
        String view = memberController.showRegistrationForm(model);

        // Assert
        assertEquals("index", view);
        verify(model, times(2)).addAttribute(any(), any());
    }

    @Test
    public void testAddMember_validInput() {
        // Arrange
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "john.doe@example.com", "1234567890");

        // Act
        String view = memberController.addMember(memberRequestDTO, redirectAttributes, bindingResult);

        // Assert
        assertEquals("redirect:/members", view);
        verify(memberRestController, times(1)).createMember(any());
    }

    @Test
    public void testAddMember_invalidInput() {
        // Arrange
        MemberRequestDTO memberRequestDTO = new MemberRequestDTO("John Doe", "john.doe@example.com", "1234567890");

        // Act
        String view = memberController.addMember(memberRequestDTO, redirectAttributes, bindingResult);

        // Assert
        assertEquals("redirect:/members", view);
    }

    @Test
    public void testDeleteMember() {
        // Act
        String view = memberController.deleteMember(1L);

        // Assert
        assertEquals("redirect:/members", view);
        verify(memberRestController, times(1)).deleteMemberById(1L);
    }


}