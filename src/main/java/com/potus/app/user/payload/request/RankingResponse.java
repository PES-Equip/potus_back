package com.potus.app.user.payload.request;



import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;



public record RankingResponse(Long id, String username,int level, int current){}




