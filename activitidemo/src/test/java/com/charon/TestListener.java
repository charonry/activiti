package com.charon;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/21 9:47
 * @Version 1.0
 **/
public class TestListener {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/demo-listen.bpmn";

    //流程定义文件key
    final static String KEY= "testListener";


    /**
     * 流程部署
     *act_re_deployment  流程部署
     *act_re_procdef     流程定义 一对多关系
     *act_ge_bytearray   流程资源
     */
    @Test
    public void testDeploymentListen(){
        //1、创建ProcessEngine
        //2、获取RepositoryServcie
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、使用service进行流程的部署，定义一个流程的名字，把bpmn和png部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-listen")
                .key("myEvectionTest-listen")
                .addClasspathResource(BPMN)
                .deploy();
        //4、输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }

    @Test
    public void startDemoListener(){
        //获取流程引擎
        //获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程实例
        runtimeService.startProcessInstanceByKey(KEY);

    }
}
