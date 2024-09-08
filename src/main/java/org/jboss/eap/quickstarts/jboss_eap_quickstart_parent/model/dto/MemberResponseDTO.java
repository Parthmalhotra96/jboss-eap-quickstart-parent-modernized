package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;

public record MemberResponseDTO(Long id, String name, String email, String phoneNumber) {
    public static MemberResponseDTO fromMember(Member member) {
        return new MemberResponseDTO(member.getId(), member.getName(), member.getEmail(), member.getPhoneNumber());
    }
}
