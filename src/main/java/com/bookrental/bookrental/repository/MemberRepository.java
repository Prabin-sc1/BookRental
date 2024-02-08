package com.bookrental.bookrental.repository;

import com.bookrental.bookrental.model.Book;
import com.bookrental.bookrental.model.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Modifying
    @Query(value = "update tbl_member set is_active = false where id = :id", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

    @Query(value = "select * from tbl_member where email = :email ", nativeQuery = true)
    Member findByEmail(@Param("email") String email);
    Optional<Member> findByName(String name);
}
