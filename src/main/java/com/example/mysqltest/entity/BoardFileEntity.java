package com.example.mysqltest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "board_file_table")
public class BoardFileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity , String originalName, String storedFileName) {
         BoardFileEntity boardFileEntity = new BoardFileEntity();
         boardFileEntity.setOriginalFileName(originalName);
         boardFileEntity.setStoredFileName(storedFileName);
         boardFileEntity.setBoardEntity(boardEntity);
         return boardFileEntity;
    }
}
