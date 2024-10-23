package com.bookrental.bookrental.mapper;

import com.bookrental.bookrental.pojo.author.AuthorResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AuthorMapper {
    @Select("select ta.id, ta.email, ta.mobile_number as mobileNumber, ta.\"name\"  from tbl_author ta where ta.is_active")
    List<AuthorResponsePojo> getAllAuthor();

    @Select(" select ta.id, ta.email, ta.mobile_number as mobileNumber, ta.\"name\"  from tbl_author ta where ta.is_active and  ta.id = #{id}")
    Optional<AuthorResponsePojo> getSingleAuthor(@Param("id") Integer id);

    @Select("SELECT ta.id, ta.email, ta.mobile_number AS mobileNumber, ta.\"name\" " +
            "FROM tbl_author ta " +
            "WHERE ta.is_active = TRUE AND ta.email = #{email}")
    Optional<AuthorResponsePojo> getAuthorDetailsByEmail(@Param("email") String email);

}
