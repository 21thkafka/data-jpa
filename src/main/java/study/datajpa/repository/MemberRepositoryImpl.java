package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor    // 클래스 명 규칙: 레포지토리 이름 + Impl
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    //@RequiredArgsConstructor 쓰면 생략가능
/*    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }*/

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
