package com.charon;

import com.charon.entity.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ActivitiGatewayParallel {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/evection-parallel.bpmn";

    //流程定义文件key
    final static String KEY= "parallel";

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
                .name("出差申请-并行网关部署")
                .key("myEvectionTest-parallel")
                .addClasspathResource(BPMN)
                .deploy();
        //4、输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }


    /**
     * 启动流程
     */
    @Test
    public void testStartProcess(){
        // 获取流程引擎
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 创建变量集合
        Map<String, Object> map = new HashMap<String,Object>();
        // 创建出差pojo对象
        Evection evection = new Evection();
        // 设置出差天数
        evection.setNum(4d);
        // 定义流程变量，把出差pojo对象放入map
        map.put("evection",evection);
        // 启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(KEY, map);
        // 输出
        System.out.println("流程实例名称="+processInstance.getName());
        System.out.println("流程定义id=="+processInstance.getProcessDefinitionId());
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completTask(){
        //任务负责人
        String assingee = "rose";
        //获取流程引擎
        //获取taskservice
        TaskService taskService = processEngine.getTaskService();
        //查询任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee(assingee)
                .singleResult();
        if(task != null){
            //根据任务id来完成任务
            taskService.complete(task.getId());
        }

    }
}
