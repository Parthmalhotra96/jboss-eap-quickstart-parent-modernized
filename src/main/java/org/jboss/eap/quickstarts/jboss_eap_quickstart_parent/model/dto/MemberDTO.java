package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.dto;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;

public record MemberDTO(Long id, String name, String email, String phoneNumber) {
    public static MemberDTO fromMember(Member member) {
        return new MemberDTO(member.getId(), member.getName(), member.getEmail(), member.getPhoneNumber());
    }
}
