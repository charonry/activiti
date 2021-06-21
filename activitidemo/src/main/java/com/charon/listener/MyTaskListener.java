package com.charon.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @Description
 * @Author tangkang
 * @Date 2021/6/21 9:34
 * @Version 1.0
 **/
public class MyTaskListener  implements TaskListener {

    public void notify(DelegateTask delegateTask) {
        //判断当前的任务 是 创建申请 并且  是 create事件
        if("创建申请".equals(delegateTask.getName()) &&
                "create".equals(delegateTask.getEventName())){
            delegateTask.setAssignee("张三");
        }
    }
}
