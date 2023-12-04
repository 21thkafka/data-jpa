package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

 //   @Query(name = "Member.findByUsername")    //이부분 주석처리 해도 도메인의 네임드쿼리를 먼저 찾아보고 실행해서 문제 발생 안함
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);   // 컬렉션

    Member findMemberByUsername(String username);       // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

   @Query(value = "select m from Member m left join m.team t",
           countQuery = "select count(m) from Member m")    // 카운트 쿼리 따로 작성하여 성능 향상시킬 수 있음
   Page<Member> findByAge(int age, Pageable pageable);

   @Modifying(clearAutomatically = true)   //벌크 업데이트할때 필요, 영속성 상관없이 바로 반영
   @Query("update Member m set m.age = m.age+1 where m.age >= :age")
   int bulkAgePlus(@Param("age") int age);

   @Query("select m from Member m left join fetch m.team")
   List<Member> findMemberFetchJoin();

   @Override
   @EntityGraph(attributePaths = {"team"})  //fetch join
   List<Member> findAll();

   @EntityGraph(attributePaths = {"team"})
   @Query("select m from Member m")
   List<Member> findMemberEntityGraph();

   //@EntityGraph(attributePaths = ("team"))
   @EntityGraph("Member.all")
   List<Member> findEntityGraphByUsername(@Param("username") String username);

   @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
   Member findReadOnlyByUsername(String username);

   //select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
