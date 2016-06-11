package com.matto.user.service;

import com.matto.user.dao.IUserDao;
import com.matto.user.pojo.TUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by matto on 16-6-11.
 */
@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private IUserDao userDao ;

    @Override
    public TUser getUserById(int id) {
        return userDao.getUserById(id);
    }
}
