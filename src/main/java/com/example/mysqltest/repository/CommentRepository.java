package com.example.mysqltest.repository;

import com.example.mysqltest.DTO.CommentDTO;
import com.example.mysqltest.entity.BoardEntity;
import com.example.mysqltest.entity.BoardFileEntity;
import com.example.mysqltest.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.xml.stream.events.Comment;
import java.util.ArrayList;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity , Long> {
    // select * from comment_table where board_id = ? order by desc ;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);

}
