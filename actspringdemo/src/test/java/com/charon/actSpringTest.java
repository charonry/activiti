package com.charon;

import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/21 16:51
 * @Version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:activiti-spring.xml")
public class actSpringTest {

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void testRep(){
        System.out.println(repositoryService);
    }
}
