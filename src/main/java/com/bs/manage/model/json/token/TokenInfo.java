package com.bs.manage.model.json.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 2020/6/10 16:27
 * fzj
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class TokenInfo implements Serializable {

    private static final long serialVersionUID = -1950074913041906823L;

    private String token;       //token
    private long expire_time;   //过期时间,单位秒
}
