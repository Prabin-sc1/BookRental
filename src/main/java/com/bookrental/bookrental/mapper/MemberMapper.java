package com.bookrental.bookrental.mapper;

import com.bookrental.bookrental.pojo.member.MemberResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {
    @Select("select tm.id, tm.\"name\",tm.email , tm.address, tm.mobile_number as mobileNumber from tbl_member tm where tm.is_active")
    List<MemberResponsePojo> getAllMember();

    @Select("select tm.id, tm.\"name\",tm.email , tm.address, tm.mobile_number as mobileNumber from tbl_member tm where tm.is_active and tm.id = #{id}")
    Optional<MemberResponsePojo> getSingleMember(@Param("id") Integer id);

}
