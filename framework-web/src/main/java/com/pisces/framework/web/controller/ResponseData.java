package com.pisces.framework.web.controller;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应数据
 *
 * @author jason
 * @date 2022/12/08
 */
@Getter
@Setter
public class ResponseData implements Serializable {
    @Serial
    private static final long serialVersionUID = -5405084202383222826L;

    private boolean success;
    private int status;
    private String name = "";
    private String message = "";
    private String exception = "";
    private Object data;
}
