package com.bookrental.bookrental.mapper;

import com.bookrental.bookrental.model.User;
import com.bookrental.bookrental.pojo.user.UserResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    @Select("select tu.id, tu.email, tr.\"name\"  from tbl_user tu join tbl_role tr on tu.id = tr.id;")
    List<UserResponsePojo> allUser();

//    @Select("select tu.id, tu.email from tbl_user tu where tu.id = #{id}")
//    Optional<UserResponsePojo> getSingleUser(@Param("id") Integer id);

    @Select("select tu.id, tu.email from tbl_user tu where tu.id = #{id}")
    Optional<UserResponsePojo> getSingleUser(@Param("id") Integer id);
}
