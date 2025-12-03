package com.springboot.demo.intercepter;

import com.alibaba.fastjson2.JSON;
import com.springboot.demo.annotation.Role;
import com.springboot.demo.code.CodeCaption;
import com.springboot.demo.code.PubCode;
import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.model.bean.account.User;
import com.springboot.demo.model.bean.account.UserRole;
import com.springboot.demo.model.json.ResponseJson;
import com.springboot.demo.service.user.impl.UserServiceImpl;
import com.springboot.demo.until.Md5Util;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * 2020/1/17 11:01
 * fzj
 */
@Slf4j
@Component
@AllArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {


    private final UserToken userToken;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Allow", "*");
        try {
            //是否带有token
            String encode64Token = request.getParameter("token");
            if (!(StringUtils.hasText(encode64Token) && encode64Token.matches(Md5Util.TOKEN_MATCHERS))) {
                throw new BusinessException(PubCode.LOGIN_ERROR.code, PubCode.LOGIN_ERROR.message);
            }

            //token中登录时间是否已经过期
            String originToken = Md5Util.getOriginToken(encode64Token);
            LocalDateTime loginDateTime = null;
            try {
                loginDateTime = Md5Util.decodeTokenForDateTime(encode64Token);
            } catch (DateTimeParseException dateTimeParseException) {
                log.error("{}时间格式错误", originToken);
            }
            if (loginDateTime == null) {
                throw new BusinessException(PubCode.LOGIN_ERROR.code, PubCode.LOGIN_ERROR.message);
            }
            long loginSeconds = Duration.between(loginDateTime, LocalDateTime.now()).getSeconds();
            if (loginSeconds > UserToken.maxLoginSeconds || loginSeconds < 0) {
                //清除过期的缓存
                userToken.putCache(originToken, null);
                throw new BusinessException(PubCode.LOGIN_ERROR.code, PubCode.LOGIN_ERROR.message);
            }

            //是否已经登录过
            User user = userToken.getCache(originToken);
            if (user == null) {
                throw new BusinessException(PubCode.LOGIN_ERROR.code, PubCode.LOGIN_ERROR.message);
            }
            List<Integer> roleIds = JSON.parseArray(user.getResources(),Integer.class);
            user.setRoleIds(roleIds);

            //验证权限
            if (!roleValidated(request, handler, user)) {
                throw new BusinessException(PubCode.LIMITED_AUTHORITY.code, PubCode.LIMITED_AUTHORITY.message);
            }
            //设置会话上下文
            UserToken.setContext(user);
        } catch (BusinessException e) {
            log.error(e.getMessage());
            printJson(ResponseJson.fail(e.getCode(), e.getMessage()), response);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            printJson(ResponseJson.fail(PubCode.SYSTEM_ERROR.code, PubCode.SYSTEM_ERROR.message), response);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    /**
     * 验证权限
     *
     * @param handler handlerMethod
     * @param user    用户信息
     * @return 权限是否通过
     */
    private boolean roleValidated(HttpServletRequest request, Object handler, User user) throws BusinessException {
        UserServiceImpl.checkUserStatus(user, false);

        //管理员无需权限验证
        if (CodeCaption.ROLE_ADMIN != user.getRole() && handler instanceof HandlerMethod handlerMethod) {
            boolean needValidated = false;
            boolean isAdmin = false;
            int roleId = 0;
            if (handlerMethod.getBeanType().isAnnotationPresent(Role.class)) {
                needValidated = true;
                Role role = handlerMethod.getBeanType().getAnnotation(Role.class);
                if (role.isAdmin()) {
                    isAdmin = true;
                }
                roleId = role.roleId();
            }
            if (handlerMethod.getMethod().isAnnotationPresent(Role.class)) {
                needValidated = true;
                Role role = handlerMethod.getMethod().getAnnotation(Role.class);
                if (role.isAdmin()) {
                    isAdmin = true;
                }
                if (role.except()) {
                    return true;
                }
                roleId = role.roleId();
            }
            if (isAdmin) {
                return false;
            }
            if (!needValidated) {
                return true;
            }

            //查看是否有所需权限
            if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
                return false;
            }

            if (roleId == 0) {
                String uri = request.getRequestURI();
                String uriReplaceId = uri.replaceAll("/\\d+", "/id");
                String methodType = request.getMethod();
                Optional<UserRole> userRole1 = UserRole.userRoles.stream().filter(userRole -> uriReplaceId.equals(userRole.getApi()) && methodType.equalsIgnoreCase(userRole.getMethod()))
                        .findFirst();
                if (userRole1.isPresent()) {
                    roleId = userRole1.get().getId();
                } else {
                    throw new BusinessException("请联系管理员配置权限");
                }
            }
            return user.getRoleIds().contains(roleId);

        }
        return true;
    }

    /**
     * 返回json给前端
     */
    private void printJson(ResponseJson json, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSONString(json));
        writer.flush();
        writer.close();
    }
}
