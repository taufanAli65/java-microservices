package com.bootcamp.trade_service.controller;

import com.bootcamp.trade_service.dto.request.ReqTradePokemonDto;
import com.bootcamp.trade_service.dto.response.BaseResponse;
import com.bootcamp.trade_service.exception.BadRequestException;
import com.bootcamp.trade_service.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;
    private final HttpServletRequest request;

    @PostMapping("/exchange")
    public ResponseEntity<BaseResponse<Void>> exchangePokemon(
            @Valid @RequestBody ReqTradePokemonDto requestBody
    ) {
        Long requesterId = Long.parseLong(request.getHeader("X-Authenticated-User-Id"));
        if (requesterId.equals(requestBody.getReceiverId())) {
            throw new BadRequestException("Requester dan receiver tidak boleh sama");
        }

        tradeService.tradePokemon(
                requesterId,
                requestBody.getReceiverId(),
                requestBody.getMyPokemonId(),
                requestBody.getReceiverPokemonId()
        );

        String message = "Pokemon %s milik %s telah berhasil ditukar dengan pokemon %s milik %s"
                .formatted(
                        requestBody.getMyPokemonId(),
                        requesterId,
                        requestBody.getReceiverPokemonId(),
                        requestBody.getReceiverId()
                );
        return ResponseEntity.ok(BaseResponse.success(message, null));
    }
}
