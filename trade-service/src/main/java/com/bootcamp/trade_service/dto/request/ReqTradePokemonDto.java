package com.bootcamp.trade_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReqTradePokemonDto {
    @NotNull(message = "receiverId wajib diisi")
    private Long receiverId;

    @NotBlank(message = "myPokemonId wajib diisi")
    private String myPokemonId;

    @NotBlank(message = "receiverPokemonId wajib diisi")
    private String receiverPokemonId;
}
