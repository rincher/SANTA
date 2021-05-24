package sparta.enby.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.enby.service.SearchService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/board/search")
    public ResponseEntity<Map<String,Object>> boardSearch(@RequestParam("Keyword") String keyword){
        return searchService.boardSearch(keyword);
    }

    @GetMapping("/review/search")
    public ResponseEntity<Map<String,Object>> reviewSearch(@RequestParam("Keyword") String keyword){
        return searchService.reviewSearch(keyword);
    }
}
