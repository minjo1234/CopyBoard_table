package com.example.mysqltest.service;


import com.example.mysqltest.DTO.CommentDTO;
import com.example.mysqltest.entity.BoardEntity;
import com.example.mysqltest.entity.CommentEntity;
import com.example.mysqltest.repository.BoardRepository;
import com.example.mysqltest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentSerivce {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO) {
        /*
        부모엔티티(BoardEntity) 조회
         */
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());
        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }

    public List<CommentDTO> findAll(Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for(CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity , boardId);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
        // select * from comment_table where board_id = ? order by desc ;
    }
}
