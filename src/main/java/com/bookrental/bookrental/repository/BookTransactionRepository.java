package com.bookrental.bookrental.repository;

import com.bookrental.bookrental.model.BookTransaction;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Integer> {
    @Query(value = "SELECT * FROM tbl_book_transaction tbt WHERE tbt.code = :code AND tbt.member_id = :memberId AND tbt.book_id = :bookId and tbt.rent_status ='RENT' ", nativeQuery = true)
    Optional<BookTransaction> findByCodeAndMemberIdAndBookId(
            @Param("code") String code,
            @Param("memberId") Integer memberId,
            @Param("bookId") Integer bookId
    );

    @Modifying
    @Query(value = "update tbl_book_transaction set active_closed = false where id = :id", nativeQuery = true)
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM tbl_book_transaction tbt WHERE tbt.code = :code and tbt.rent_status ='RENT' ", nativeQuery = true)
    Optional<BookTransaction> findTransactionByCode(
            @Param("code") String code
    );
}
