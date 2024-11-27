package org.devkirby.hanimman.controller;

import org.devkirby.hanimman.dto.MarketDTO;
import org.devkirby.hanimman.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;

    //ID로 마켓 조회
    @GetMapping("/{id}")
    public ResponseEntity<MarketDTO> getMarketById(@PathVariable Integer id) {
        MarketDTO marketDTO = marketService.getMarketById(id);
        //System.out.println(marketDTO);
        return ResponseEntity.ok(marketDTO); // 200 OK 반환
        }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MarketDTO>> getMarketsByCategory(@PathVariable Integer categoryId) {
        List<MarketDTO> markets = marketService.getMarketsByCategoryId(categoryId);
        System.out.println(markets);
        return ResponseEntity.ok(markets);
    }

}