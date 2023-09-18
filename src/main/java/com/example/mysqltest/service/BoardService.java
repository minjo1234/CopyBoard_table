package com.example.mysqltest.service;

import com.example.mysqltest.DTO.BoardDTO;
import com.example.mysqltest.entity.BoardEntity;
import com.example.mysqltest.entity.BoardFileEntity;
import com.example.mysqltest.repository.BoardFileRepository;
import com.example.mysqltest.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// DTO -> ENTITY
// ENTITY -> DTO
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    public void save(BoardDTO boardDTO) throws IOException {
        // 파일 첨부 여부에 따라 로직 분리
        if (boardDTO.getBoardFile().isEmpty()){
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 첨부파일있음

            /*
                1.DTO 담긴 파일을 꺼냄
                2.파일의 이름 가져옴
                3.서버 저장용 이름을 만듦
                4.저장 경로 설정
                5.해당 경로에 파일 저장
                6.board_table에 해당 데이터 Save처리
                7.board_file_table에 해당 데이터 처리
             */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            // entity에서 가져와서 그거에 대한 정보를 어느정도 fileEntity에 넣어야하니까 넣고 가져온다 .
            // entity상태에서 가져와서 필요한 정보를 바로 board_file_table 에 넣을수있도록 설정한다 .
            Long savedId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(savedId).get();

            for (MultipartFile boardFile : boardDTO.getBoardFile()) { // 1

                String originalFileName = boardFile.getOriginalFilename(); // 2
                String storedFileName = System.currentTimeMillis() + "-" + originalFileName;
                String savepath = "/Users/jomin/springboot_img/" + storedFileName;
                boardFile.transferTo(new File(savepath));
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);

            }
        }

    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    // rollbaCk이 가능하도록 직접커리문을 작성해주었으므로 이걸 사용해주는것이다 .
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }
    // 아이디를 고정된값으로 사용했으므로 가능하다 .

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; // 한 페이지에 보여줄 글 갯수
        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        // page 위치 있는 값은 0 부터 시작
        //
        Page<BoardEntity> boardEntities =
            boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 목록 : id, writer, title,
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle() , board.getBoardHits() , board.getCreatedTime()));
        return boardDTOS;
    }
}
