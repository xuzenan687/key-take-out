package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        // 记录用户登录信息，用于审计或调试目的
        log.info("用户登录：{}", userLoginDTO);

        // 调用微信登录服务，实现用户登录逻辑
        User user = userService.wxLogin(userLoginDTO);

        // 初始化JWT声明，用于存储用户信息
        Map<String, Object> claims=new HashMap<>();

        // 将用户ID放入JWT声明中，以便在令牌中携带用户信息
        claims.put(JwtClaimsConstant.USER_ID,user.getId());

        // 创建JWT令牌，包含用户信息和有效期
        String token=JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        // 构建用户登录响应对象，包含用户ID、openid和令牌
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        // 返回用户登录成功的结果
        return Result.success(userLoginVO);

    }
}
