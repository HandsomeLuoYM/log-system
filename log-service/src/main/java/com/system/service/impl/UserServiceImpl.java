package com.system.service.impl;

import com.system.dao.UserMapper;
import com.system.po.User;
import com.system.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Ming
 * @date 2021/9/23 - 15:43
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User hello() {
        return userMapper.selectById(1L);
    }
}
