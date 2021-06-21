package com.charon;

import com.charon.entity.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/21 10:20
 * @Version 1.0
 **/
public class TestVariables {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/evection-global.bpmn";

    //流程定义文件key
    final static String KEY= "myEvection2";


    /**
     * 流程部署
     *act_re_deployment  流程部署
     *act_re_procdef     流程定义 一对多关系
     *act_ge_bytearray   流程资源
     */
    @Test
    public void testDeployment(){
        //1、创建ProcessEngine
        //2、获取RepositoryServcie
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、使用service进行流程的部署，定义一个流程的名字，把bpmn和png部署到数据中
        Deployment deploy = repositoryService.createDeployment()
                .name("出差申请流程-流程变量")
                .key("myEvectionTest-variable")
                .addClasspathResource(BPMN)
                .deploy();
        //4、输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }


    /**
     * 启动流程 的时候设置流程变量
     * 设置流程变量num
     * 设置任务负责人
     */
    @Test
    public void testStartProcess(){
        //获取流程引擎
        //获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //流程变量的map
        Map<String,Object> variables = new HashMap<String,Object>();
        /*//设置流程变量
        Evection evection = new Evection();
        //设置出差日期
        evection.setNum(3d);
        // 把流程变量的pojo放入map
        variables.put("evection",evection);*/
        //设定任务的负责人
        variables.put("assignee0","李四");
        variables.put("assignee1","王经理");
        variables.put("assignee2","杨总经理");
        variables.put("assignee3","张财务");
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY, variables);
        System.out.println(processInstance.getId());
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completTask(){
        //任务负责人
        String assingee = "张财务";
        Evection evection = new Evection();
        evection.setNum(2d);
        Map<String,Object> variablesMap = new HashMap<String,Object>();
        variablesMap.put("evection",evection);
        //获取流程引擎
        //获取taskservice
        TaskService taskService = processEngine.getTaskService();
        //查询任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee(assingee)
                // 流程实例Id
                .processInstanceId("50001")
                .singleResult();
        if(task != null){
            //根据任务id来完成任务
            taskService.complete(task.getId(),variablesMap);
        }

    }



}
