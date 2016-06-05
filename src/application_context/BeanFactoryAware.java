package application_context;

import factory.BeanFactory;

/**
 * 顶层接口，定义了对实现该接口的对象在赋值时所需要调用的方法
 * Created by MiaoZhuang on 2016/6/2.
 */
public interface BeanFactoryAware {

    void setBeanFactory(BeanFactory beanFactory);
}
