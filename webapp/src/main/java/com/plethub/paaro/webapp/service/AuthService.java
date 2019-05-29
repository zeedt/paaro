package com.plethub.paaro.webapp.service;

import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ChangePassword;
import com.plethub.paaro.webapp.dto.ForgotPassword;
import com.plethub.paaro.webapp.dto.LoginForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface AuthService {

    String login(LoginForm loginForm, HttpSession httpSession);

    UserDetails getCurrentUser(HttpServletRequest hsr);

    String forgotPassword(ForgotPassword forgotPassword);

    String logout(UserDetails userDetails);

    String changePassword(ChangePassword changePassword, HttpSession httpSession);
}
