package com.lcwd.user.service.payload;

import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponser {
    private String message;
    private boolean sucess;
    private HttpStatus httpStatus;
}
