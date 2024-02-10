package com.flab.offcoupon.util;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private static final String MEMBER_ID = "LOGIN_MEMBER_ID";
    private final HttpSession session;

    public String getLoginMember() {
        return (String) session.getAttribute(MEMBER_ID);
    }

    public void setLoginMember(String id) {
        session.setAttribute(MEMBER_ID, id);
    }

    public void removeLogoutMember() {
        session.removeAttribute(MEMBER_ID);
    }
}
