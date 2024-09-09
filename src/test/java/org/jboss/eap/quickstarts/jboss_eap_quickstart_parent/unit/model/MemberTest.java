package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.unit.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberTest {

    private Member member;

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
    }

    @Test
    public void testValidMember() {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidName() {
        member.setName("John123");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("Must not contain numbers", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail() {
        member.setEmail("john_doe.com");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidPhoneNumber() {
        member.setPhoneNumber("12345");

        Set<ConstraintViolation<Member>> violations = validator.validate(member);
        assertEquals(1, violations.size());
        assertEquals("size must be between 10 and 12", violations.iterator().next().getMessage());
    }
}

