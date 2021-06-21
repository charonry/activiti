package com.charon;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/17 15:45
 * @Version 1.0
 **/
public class TestActiviti {

    //工作流引擎
    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    //流程部署文件名称
    final static String BPMN= "bpmn/evection.bpmn";
    final static String PNG= "bpmn/evection.png";
    //流程定义文件Id
    final static String DEPLOYMENTID= "myEvection:1:4";
    //流程定义文件key
    final static String KEY= "myEvection";
    //流程实例id
    final static String FLOWID= "2501";
    //任务id
    final static String TASKID= "2505";


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
                .name("出差申请流程")
                .key("myEvectionTest")
                .addClasspathResource(BPMN)
                .addClasspathResource(PNG)
                .deploy();
        //4、输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }


    /**
     * 使用zip包进行批量的部署
     */
    @Test
    public void deployProcessByZip(){
        //获取流程引擎
        //获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //流程部署
        //读取资源包文件，构造成inputStream
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");
        //用inputStream 构造ZipInputStream
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //使用压缩包的流进行流程的部署
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署的名称="+deploy.getName());
    }


    /**
     * 启动流程实例
     * `act_hi_actinst`    流程实例执行历史信息
     * `act_hi_identitylink` 流程参与用户的历史信息
     * `act_hi_procinst`     流程实例的历史信息
     * `act_hi_taskinst`     流程任务的历史信息
     * `act_ru_execution`    流程执行信息
     * `act_ru_identitylink`  流程的正在参与用户信息
     * `act_ru_task`         流程当前任务信息
     */
    @Test
    public void testStartProcess(){
        //1、创建ProcessEngine
        //2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3、根据流程定义的id启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(KEY);
        //4、输出内容
        System.out.println("流程定义ID："+instance.getProcessDefinitionId());
        System.out.println("流程实例ID："+instance.getId());
        System.out.println("当前活动的ID："+instance.getActivityId());
    }


    /**
     * 查询个人待执行的任务
     *act_ru_task
     */
    @Test
    public void testFindPersonalTaskList(){
        //1、获取流程引擎
        //2、获取taskService
        TaskService taskService = processEngine.getTaskService();
        //3、根据流程key 和 任务的负责人 查询任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(KEY) //流程Key
                .taskAssignee("zhangsan")  //要查询的负责人
                .list();
        //4、输出
        for (Task task : taskList) {
            System.out.println("流程实例id="+task.getProcessInstanceId());
            System.out.println("任务Id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
        }
    }

    /**
     * 完成个人任务
     *act_hi_taskinst
     *act_hi_actinst
     *act_hi_identitylink
     *act_ru_task
     *act_ru_execution
     */
    @Test
    public void completTask(){
        //获取引擎
        //获取操作任务的服务 TaskService
        TaskService taskService = processEngine.getTaskService();
        //完成任务,参数：任务id,完成zhangsan的任务
        //taskService.complete(TASKID);
        /*//获取jerry - myEvection 对应的任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee("jerry")
                .singleResult();*/
        /*//完成jack的任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee("jack")
                .singleResult();*/
        //完成rose的任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(KEY)
                .taskAssignee("rose")
                .singleResult();


        System.out.println("流程实例id="+task.getProcessInstanceId());
        System.out.println("任务Id="+task.getId());
        System.out.println("任务负责人="+task.getAssignee());
        System.out.println("任务名称="+task.getName());
        //完成jerry的任务 、完成jack的任务、完成rose的任务
        taskService.complete(task.getId());
    }


    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinition(){
        //获取引擎
        //获取  Repositoryservice
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取ProcessDifinitionQuery对象
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        /*查询当前所有的流程定义 ,返回流程定义信息的集合
        processDefinitionKey（流程定义Key）
        orderByProcessDefinitionVersion 进行排序
        desc 倒叙
        list 查询出所有的内容*/
        List<ProcessDefinition> definitionList = definitionQuery.processDefinitionKey(KEY)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        //输出信息
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义ID："+processDefinition.getId());
            System.out.println("流程定义名称:"+processDefinition.getName());
            System.out.println("流程定义Key:"+processDefinition.getKey());
            System.out.println("流程定义版本:"+processDefinition.getVersion());
            System.out.println("流程部署ID:"+processDefinition.getDeploymentId());
        }
    }


    /**
     * 删除流程部署信息
     * `act_ge_bytearray`
     * `act_re_deployment`
     * `act_re_procdef`
     * 当前的流程如果并没有完成，想要删除的话需要使用特殊方式，原理就是 级联删除
     */
    @Test
    public void deleteDeployMent(){
        //获取流程引擎
        //通过引擎来获取 RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //通过部署id来删除流程部署信息
        String deploymentId = "100001";
        //repositoryService.deleteDeployment(deploymentId);
        // 删除未完成的流程
        repositoryService.deleteDeployment(deploymentId,true);
    }


    /**
     * 下载 资源文件
     * 方案1： 使用Activiti提供的api，来下载资源文件,保存到文件目录
     * 方案2： 自己写代码从数据库中下载文件，使用jdbc对blob 类型，clob类型数据读取出来，保存到文件目录
     * 解决Io操作：commons-io.jar
     * 这里我们使用方案1，Activiti提供的api：RespositoryService
     */
    @Test
    public void getDeployment() throws IOException {
         //1、得到引擎
        //2、获取api，RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3、获取查询对象 ProcessDefinitionQuery查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(KEY)
                .singleResult();
        //4、通过流程定义信息，获取部署ID
        String deploymentId = processDefinition.getDeploymentId();
        //5、通过RepositoryService ，传递部署id参数，读取资源信息（png 和 bpmn）
          //5.1、获取png图片的流
        //从流程定义表中，获取png图片的目录和名字
        String pngName = processDefinition.getDiagramResourceName();
        //通过 部署id和 文件名字来获取图片的资源
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, pngName);
          //5.2、获取bpmn的流
        String bpmnName = processDefinition.getResourceName();
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, bpmnName);
        //6、构造OutputStream流
        File pngFile = new File("d:/evectionflow.png");
        File bpmnFile = new File("d:/evectionflow.bpmn");
        FileOutputStream pngOutStream = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutStream = new FileOutputStream(bpmnFile);
        //7、输入流，输出流的转换
        IOUtils.copy(pngInput,pngOutStream);
        IOUtils.copy(bpmnInput,bpmnOutStream);
        //8、关闭流
        pngOutStream.close();
        bpmnOutStream.close();
        pngInput.close();
        bpmnInput.close();
    }


    /**
     * 查看历史信息
     */
    @Test
    public void findHistoryInfo(){
        //获取引擎
        //获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        //获取 actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        // actinst表，条件：根据 InstanceId 查询
        instanceQuery.processInstanceId("17501");
        //查询 actinst表，条件：根据 DefinitionId 查询
        //instanceQuery.processDefinitionId("myEvection:1:15004");
        //增加排序操作,orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        //查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
        //输出
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }
    }
}
