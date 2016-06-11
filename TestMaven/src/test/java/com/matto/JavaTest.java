package com.matto;


import com.matto.user.pojo.TUser;
import com.matto.user.service.IUserService;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 16-6-11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-mvc.xml", "classpath:spring-mybatis.xml"})
@Transactional
public class JavaTest {

    public static void main(String[] args) {

        System.out.println("---------------");

    }

    @Autowired
    private IUserService userService;

    @Test
    public void test() {
        TUser user = userService.getUserById(1);
        System.out.println(user.getUserName());
        System.out.println(user.getPassword());
    }


}
