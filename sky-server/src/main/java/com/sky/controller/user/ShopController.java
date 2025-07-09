package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/{status}")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){
        log.info("获取店铺状态");
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        log.info("店铺状态为：{}",status);
        return Result.success(status);
    }
}
