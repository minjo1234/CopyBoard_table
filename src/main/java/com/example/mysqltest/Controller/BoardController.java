package com.example.mysqltest.Controller;

import com.example.mysqltest.DTO.BoardDTO;
import com.example.mysqltest.DTO.CommentDTO;
import com.example.mysqltest.repository.CommentRepository;
import com.example.mysqltest.service.BoardService;
import com.example.mysqltest.service.CommentSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentSerivce commentSerivce;
    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("BoardDTO :" + boardDTO);
        boardService.save(boardDTO);

        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList" , boardDTOList);
        return "list";

    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model ,
        @PageableDefault(page=1) Pageable pageable){
        // 해당 게시글의 조회수를 하나 올리고
        // 게시글 데이터를 가져와서 detail.html에 출력하기 .

        boardService.updateHits(id);

        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> commentDTOList = commentSerivce.findAll(id);
        model.addAttribute("commentList" , commentDTOList);
        model.addAttribute("board" , boardDTO);
        model.addAttribute("page" , pageable.getPageNumber());
        return "detail";
     }

     @GetMapping("/update/{id}")
        public String updateForm(@PathVariable Long id , Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate" , boardDTO);
        return "update";
     }

     @PostMapping("/update")
     public String update(@ModelAttribute BoardDTO boardDTO , Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board" , board);
        return "detail";
//        return "redirect:/board/" + boardDTO.getId();
     }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }

    // page의 기본값을 1로 하고 Pageabel 객체를 생성한다 .
    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable , Model model) {
        // 한페이지당 3개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        // 원래는 페이지가 1부터10이라치면 startpage
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) -1 ) * blockLimit + 1;
        // endpage가 보여져야할 페이지보다 더 큰경우에는 보여져야할 페이지로 보여진다 .
        // 전체페이지가 startPage + blockLimit - 1 보다 클 경우는 사실 경우의 수가 없는데 작은 경우에는 그걸로 맞춰주는것이다 .
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit -1 : boardList.getTotalPages();
        // page 갯수 20
        // 현재 사용자가 3페이지
        // 1, 2, 3, 4, 5
        // 현재 사용자가 7페이지
        // 7,8,9
        // 보여지는 페이지 갯수 3개
        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "paging";
    }

}
