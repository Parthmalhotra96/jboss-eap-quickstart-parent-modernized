package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;

public record MemberRequestDTO(String name, String email, String phoneNumber) {
}
