package com.lawfirm.casemanagement.security;

import lombok.Data;

@Data
public class UserPrincipal {
    private Long id;
    private String username;
    private String realName;
    private String role;
}
