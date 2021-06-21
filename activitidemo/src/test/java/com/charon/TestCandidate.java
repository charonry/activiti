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
 * @Date 2021/6/21 11:22
 * @Version 1.0
 **/
public class TestCandidate {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/evection-candidate.bpmn";

    //流程定义文件key
    final static String KEY= "testCandidate";



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
                .name("出差申请流程-组任务")
                .key("myEvectionTest-candidate")
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
        //获取流程引擎
        //获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(KEY);
        System.out.println(processInstance.getId());
    }


    /**
     * 完成个人任务
     */
    @Test
    public void completTask(){
        //任务负责人
        String assingee = "汤姆";
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

    /**
     * 查询组任务
     * act_ru_identitylink
     */
    @Test
    public void findGroupTaskList(){
        //任务候选人
        String candidateUser = "wangwu";
        //获取引擎
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //查询组任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskCandidateUser(candidateUser) //根据候选人查询任务
                .list();
        for (Task task : taskList) {
            System.out.println("========================");
            System.out.println("流程实例ID="+task.getProcessInstanceId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
        }
    }


    /**
     * 候选人拾取任务
     */
    @Test
    public void claimTask(){
        //获取引擎
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //当前任务的id
        String taskId = "65002";
        //任务候选人
        String candidateUser = "wangwu";
        //查询任务
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(candidateUser)
                .singleResult();
        if(task != null){
//            拾取任务
            taskService.claim(taskId,candidateUser);
            System.out.println("taskid-"+taskId+"-用户-"+candidateUser+"-拾取任务完成");
        }
    }


    /**
     * 任务的归还
     */
    @Test
    public void testAssigneeToGroupTask(){
        //获取引擎
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //当前任务的id
        String taskId = "65002";
        //任务负责人
        String assignee = "wangwu";
        //根据key 和负责人来查询任务
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if(task != null){
            // 归还任务 ,就是把负责人设置为空
            taskService.setAssignee(taskId,null);
            System.out.println("taskid-"+taskId+"-归还任务完成");
        }
    }

    /**
     * 任务的交接
     */
    @Test
    public void testAssigneeToCandidateUser(){
        //获取引擎
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //当前任务的id
        String taskId = "65002";
        //任务负责人
        String assignee = "wangwu";
        //任务候选人
        String candidateUser = "lisi";
        //根据key 和负责人来查询任务
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(assignee)
                .singleResult();
        if(task != null){
            //交接任务 ,就是把负责人 设置为空
            taskService.setAssignee(taskId,candidateUser);
            System.out.println("taskid-"+taskId+"-交接任务完成");
        }
    }
}
