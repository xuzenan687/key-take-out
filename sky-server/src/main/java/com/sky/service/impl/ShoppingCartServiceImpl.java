package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    public void add(ShoppingCartDTO shoppingCartDTO){
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);
        //判断当前商品是否在购物车中
        if(list!=null&&list.size()>0){
            ShoppingCart cart = list.get(0);
            Integer currentNumber = cart.getNumber();
            if (currentNumber == null) {
                currentNumber = 0; // 设置默认值为 0
            }
            cart.setNumber(currentNumber+1);
            shoppingCartMapper.updateNumberById(cart);
        }else{
            //判断是套餐还是菜品
            Long setmealId=shoppingCart.getSetmealId();
            Long dishId=shoppingCart.getDishId();
            if(setmealId!=null){
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }else{
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insert(shoppingCart);

        }




    }

    @Override
    public List<ShoppingCart> list() {
        List<ShoppingCart> list = shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
        return list;
    }

    @Override
    public void clean() {
        shoppingCartMapper.cleanByUserId(BaseContext.getCurrentId());
    }
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId=BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        shoppingCart=shoppingCartMapper.getById(shoppingCart);
        shoppingCart.setNumber(shoppingCart.getNumber()-1);
        shoppingCartMapper.updateNumberById(shoppingCart);
    }
}
