package com.bookrental.bookrental.repository;

import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.model.Book;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Modifying
    @Query(value = "update tbl_book set is_active = false where id = :id", nativeQuery = true)
    void deleteById(@Param("id") Integer id);


    @Query(value = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'tbl_book'", nativeQuery = true)
    String[] getColumnNames();

    Optional<Book> findByName(String name);
}
