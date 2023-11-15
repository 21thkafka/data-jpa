package study.datajpa.repository;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        System.out.println("memberRepository" + memberRepository.getClass());
        Member member = new Member("memberA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        AssertionsForClassTypes.assertThat(findMember1).isEqualTo(member1);
        AssertionsForClassTypes.assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member!!!!");

        //목록 조회 검증
        List<Member> all = memberRepository.findAll();
        AssertionsForClassTypes.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        AssertionsForClassTypes.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        AssertionsForClassTypes.assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        AssertionsForClassTypes.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        AssertionsForClassTypes.assertThat(result.get(0).getAge()).isEqualTo(20);
        AssertionsForClassTypes.assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void namedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        AssertionsForClassTypes.assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList){
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto){
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member m : byNames){
            System.out.println("member = " + m);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findListByUsername("aaa");   // List는 일치하는 항목 없어도 null이 아님
        System.out.println("result : " + result.size());

        Member findMember = memberRepository.findMemberByUsername("aaa");
        System.out.println("findMember : " + findMember);

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("aaa"); // 데이터 유무 확실하지 않을땐 optional
        System.out.println("optionalMember : " + optionalMember);

        //단건 반환 유형인데 복수 개의 데이터가 조회되면 예외가 터짐
        //Optional<Member> testMember = memberRepository.findOptionalByUsername("AAA");
    }


    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //Slice로 하면 1개 더 가져옴
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //dto 변환하기
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        /*for (Member member : content){
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);*/

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 29));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //벌크 연산할때 영속성 비워두는 작업이 필요, @Modifying에 옵션 있음
        //벌크 업데이트 후 조회나 마이바티스와 같이 사용하는 경우 영속성 비워드는 것이 꼭 필요
        //em.flush();
        //em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member= " + member5);

        //then
        AssertionsForClassTypes.assertThat(resultCount).isEqualTo(3);
    }
}