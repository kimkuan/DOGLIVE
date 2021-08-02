package com.ssafy.api.controller;


import com.ssafy.api.request.AdoptFormReq;
import com.ssafy.api.request.BoardRegisterPostReq;
import com.ssafy.api.request.BookmarkReq;
import com.ssafy.api.response.BoardDetailGetRes;
import com.ssafy.api.response.BoardListGetRes;
import com.ssafy.api.service.AdoptService;
import com.ssafy.api.service.BoardService;
import com.ssafy.api.service.FindService;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.auth.Bookmark;
import com.ssafy.db.entity.board.Board;
import com.ssafy.db.entity.board.BoardComment;
import com.ssafy.db.entity.board.BoardImage;
import com.ssafy.db.entity.board.DogInformation;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관련 API 요청 처리를 위한 컨트롤러 정의.
 */
@CrossOrigin(origins={"*"}, maxAge=6000)
@Api(value = "입양/임보 API", tags = {"Board"})
@RestController
@RequestMapping("/api/v1/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    AdoptService adoptService;

    @Autowired
    FindService findService;

    @GetMapping("/adopt")
    @ApiOperation(value = "입양/임보 공고 목록", notes = "입양/입양 공고 목록을 가져온다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<BoardListGetRes> adoptBoardList(){
        List<Board> boardList = adoptService.getAdoptBoardList();
        return ResponseEntity.ok(BoardListGetRes.of(200, "Success", boardList, boardList.size()));
    }

    @PostMapping("/application/{userId}")
    @ApiOperation(value = "입양/임보 공고 목록", notes = "입양/입양 공고 목록을 가져온다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> insertAdoptForm(@PathVariable("userId") String id, @RequestBody @ApiParam(value="입양 신청서 정보", required = true) AdoptFormReq adoptFormReq){
//        adoptService.insertAdoptForm(id, adoptApplicationPostReq);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "입양 신청서가 정상적으로 등록되었습니다"));
    }

    @GetMapping("/find")
    @ApiOperation(value = "실종/보호 공고 목록", notes = "입양/입양 공고 목록을 가져온다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<BoardListGetRes> findBoardList(){
        List<Board> boardList = findService.getFindBoardList();
        return ResponseEntity.ok(BoardListGetRes.of(200, "Success", boardList, boardList.size()));
    }

    @PostMapping()
    @ApiOperation(value = "게시판 공고 등록", notes = "게시판 공고를 삭제한다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> registerAdoptBoard(@RequestBody @ApiParam(value="공고 등록 정보", required = true)BoardRegisterPostReq boardRegisterPostReq){
        Board board = boardService.registerBoard(boardRegisterPostReq);
        System.out.println(board);
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "공고가 정상적으로 등록되었습니다"));
    }


    @DeleteMapping("/{boardId}")
    @ApiOperation(value = "게시판 공고 삭제", notes = "게시판 공고를 삭제한다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> deleteAdoptBoard(@PathVariable("boardId") String id){

        boardService.deleteBoard(Long.parseLong(id));
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "공고가 정상적으로 삭제되었습니다"));
    }

    @PutMapping("/{boardId}")
    @ApiOperation(value = "게시판 공고 수정", notes = "게시판 공고를 수정한다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> updateAdoptBoard(@PathVariable("boardId") String id, @RequestBody @ApiParam(value="공고 등록 정보", required = true)BoardRegisterPostReq boardRegisterPostReq){
        Board board = boardService.updateBoard(Long.parseLong(id), boardRegisterPostReq);
        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "공고가 정상적으로 수정되었습니다"));
    }

    @GetMapping("/{boardId}")
    @ApiOperation(value = "게시판 공고 상세 글 정보", notes = "게시판 공고 상세 정보를 가져온다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<BoardDetailGetRes> boardDetail(@PathVariable("boardId") String id){
        Board board = boardService.getBoardByBoardId(Long.parseLong(id));
        DogInformation dogInformation = boardService.getDogInformationByBoard(board);
        List<BoardComment> boardComments = boardService.getBoardCommentsByBoard(board);
        List<BoardImage> boardImages = boardService.getBoardImagesByBoard(board);

        return ResponseEntity.ok(BoardDetailGetRes.of(200, "Success", board, dogInformation, boardImages, boardComments));
    }




    @PostMapping("/bookmark")
    @ApiOperation(value = "게시글 북마크 등록", notes = "게시글을 북마크로 등록한다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> insertBookmark(@RequestBody @ApiParam(value="북마크 정보", required = true) BookmarkReq bookmarkReq){
        Bookmark bookmark = boardService.getBookmark(bookmarkReq);
        if(bookmark!=null) return ResponseEntity.status(409).body(BaseResponseBody.of(409, "이미 북마크로 등록된 게시물입니다"));

        System.out.println("test");
        boardService.insertBookmark(bookmarkReq);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "북마크가 정상적으로 등록되었습니다"));
    }

    @DeleteMapping("/bookmark")
    @ApiOperation(value = "게시글 북마크 삭제", notes = "게시글을 북마크에서 삭제한다.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> deleteBookmark(@RequestBody @ApiParam(value="북마크 정보", required = true) BookmarkReq bookmarkReq){
        Bookmark bookmark = boardService.deleteBookmark(bookmarkReq);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "북마크가 정상적으로 삭제되었습니다"));
    }


    @PostMapping("/adoptform/{userId}")
    @ApiOperation(value = "입양신청서 등록", notes = "입양신청서를 작성하고 저장한다")
    @ApiResponses({
            @ApiResponse(code = 204, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "사용자 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<? extends BaseResponseBody> insertAdoptForm(@RequestBody @ApiParam(value="공고 등록 정보", required = true) AdoptFormReq adoptFormReq){

        boardService.insertBookmark(bookmarkReq);

        return ResponseEntity.status(200).body(BaseResponseBody.of(200, "입양신청서가 정상적으로 등록되었습니다"));
    }


}
