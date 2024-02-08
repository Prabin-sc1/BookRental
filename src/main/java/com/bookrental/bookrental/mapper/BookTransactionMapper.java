package com.bookrental.bookrental.mapper;

import com.bookrental.bookrental.pojo.trasaction.BookTransactionOverdeuResponse;
import com.bookrental.bookrental.pojo.trasaction.BookTransactionResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookTransactionMapper {
//    @Select("select tbt.id ,tbt.code ,tbt.from_date as fromDate, tbt.to_date as toDate, tbt.book_id as bookId, tbt.member_id as memberId, tbt.rent_status as rentStatus from tbl_book_transaction tbt")
//    List<BookTransactionResponse> getAll();

    @Select("select tbt.id ,tbt.code ,tbt.from_date as fromDate, tbt.to_date as toDate,tbt.rent_status as rentStatus, tb.\"name\" as bookName, tm.\"name\" as memberName  from tbl_book_transaction tbt\n" +
            "      join tbl_book tb on tbt.book_id = tb.id  join tbl_member tm on tm.id = tbt.member_id")
    List<BookTransactionResponse> getAll();

    @Select("select tbt.id ,tbt.code ,tbt.from_date as fromDate, tbt.to_date as toDate, tbt.book_id as bookId, tbt.member_id as memberId, " +
            "tbt.rent_status as rentStatus from tbl_book_transaction tbt where id = #{id}")
    Optional<BookTransactionResponse> getById(@Param("id") Integer id);

    @Select("""
            select tbt.code ,tbt.from_date as fromDate, tbt.to_date as toDate, tb."name" as bookName ,tm."name" as memberName ,
                        tbt.rent_status as rentStatus from tbl_book_transaction tbt
                         join tbl_book tb on tbt.book_id = tb.id join tbl_member tm on tbt.member_id = tm.id  where member_id = #{id}"""
    )
    List<BookTransactionResponse> getAllTransactionByMemberId(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM tbl_book_transaction WHERE member_id = #{id} AND rent_status = #{rentStatus}")
    int countTransactionsByMemberAndRentStatus(@Param("id") int memberId, @Param("rentStatus") String rentStatus);

    @Select("""
            SELECT
                             tbt.id,
                             tbt.code,
                             tbt.from_date as fromDate,
                             tbt.to_date as toDate,
                             tb."name" as bookName ,tm."name" as memberName
                         FROM
                             tbl_book_transaction tbt join tbl_book tb on tbt.book_id = tb.id join tbl_member tm on tbt.member_id = tm.id
                         WHERE
                             tbt.to_date < CURRENT_DATE and rent_status ='RENT'
                         ORDER BY
                             from_date""")
    List<BookTransactionOverdeuResponse> overdeuList();

    @Select("""
            SELECT
                            tbt.id,
                            tbt.code,
                            tbt.from_date,
                            tbt.to_date,
                            tb."name" as bookName ,tm."name" as memberName,
                            tbt.rent_status AS rentStatus
                        FROM
                            tbl_book_transaction tbt join tbl_book tb on tbt.book_id = tb.id join tbl_member tm on tbt.member_id = tm.id
                        WHERE
                            tbt.to_date - 1 = CURRENT_DATE and rent_status ='RENT'
                        ORDER BY
                            from_date""")
    List<BookTransactionOverdeuResponse> overdeuListForReminderBeforeOneDay();




}

