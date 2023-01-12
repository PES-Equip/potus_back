package com.potus.app.admin.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class BanAccountRequest {

    @NotNull
    @NotEmpty
    private String reason;



}
