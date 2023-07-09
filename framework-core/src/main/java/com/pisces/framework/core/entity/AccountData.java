package com.pisces.framework.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 帐户数据
 *
 * @author jason
 * @date 2023/07/09
 */
@Getter
@Setter
public class AccountData {
    /**
     * 租户的编码
     */
    private Integer tenant;
    private Long dataSet;
    private String account;
    private List<String> authorities = new ArrayList<>();
}
