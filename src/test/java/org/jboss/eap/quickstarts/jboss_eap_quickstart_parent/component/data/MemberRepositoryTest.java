package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.data.MemberRepository;
import org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    public void testFindByEmail() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("12312312312");
        memberRepository.save(member);

        Optional<Member> foundMember = memberRepository.findByEmail("john.doe@example.com");
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("John Doe");
    }

    @Test
    public void testFindAllByOrderByName() {
        Member member1 = new Member();
        member1.setName("Alice");
        member1.setEmail("alice@example.com");
        member1.setPhoneNumber("12312312312");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setName("Bob");
        member2.setEmail("bob@example.com");
        member2.setPhoneNumber("9999999999");
        memberRepository.save(member2);

        List<Member> members = memberRepository.findAllByOrderByName();
        assertThat(members).hasSize(2);
        assertThat(members.get(0).getName()).isEqualTo("Alice");
        assertThat(members.get(1).getName()).isEqualTo("Bob");
    }
}

