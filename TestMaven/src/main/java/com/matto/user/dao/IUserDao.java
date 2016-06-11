package com.matto.user.dao;

import com.matto.user.pojo.TUser;

import org.springframework.stereotype.Repository;

@Repository
public interface IUserDao {

    TUser getUserById(int id);
}