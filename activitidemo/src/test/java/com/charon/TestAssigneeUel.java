package com.charon;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/19 21:19
 * @Version 1.0
 **/
public class TestAssigneeUel {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/evection-uel.bpmn";

    //流程定义文件key
    final static String KEY= "myEvection1";

    /**
     * 流程部署
     *act_re_deployment  流程部署
     *act_re_procdef     流程定义 一对多关系
     *act_ge_bytearray   流程资源
     */
    @Test
    public void testDeploymentUEL(){
        //1、创建ProcessEngine
        //2、获取RepositoryServcie
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、使用service进行流程的部署，定义一个流程的名字，把bpmn和png部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-uel")
                .key("myEvectionTest-uel")
                .addClasspathResource(BPMN)
                .deploy();
        //4、输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }


    /**
     * 启动流程实例
     * act_ru_variable
     */
    @Test
    public void testStartProcessUEL(){
        //1、创建ProcessEngine
        //2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //设定assignee的值，用来替换uel表达式
        Map<String,Object> assigneeMap = new HashMap<String,Object>();
        assigneeMap.put("assignee0","张三");
        assigneeMap.put("assignee1","李经理");
        assigneeMap.put("assignee2","王总经理");
        assigneeMap.put("assignee3","赵财务");
        //3、根据流程定义的id启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(KEY,assigneeMap);
        //4、输出内容
        System.out.println("流程定义ID："+instance.getProcessDefinitionId());
        System.out.println("流程实例ID："+instance.getId());
        System.out.println("当前活动的ID："+instance.getActivityId());
    }
}
