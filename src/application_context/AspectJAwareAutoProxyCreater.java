package application_context;

import Proxy.CglibAopProxy;
import Proxy.JdkDynamicAopProxy;
import Proxy_Factory.AdvisedSupport;
import Proxy_Factory.TargetSource;
import aspect.AspectJExpressionPointcutAdvisor;
import factory.AutoWireCapableBeanFactory;
import factory.BeanFactory;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.List;

/**
 * 在后置处理器中自动执行的代理创建器，对每一个生成的对象进行检查，若对象存在对应的Advisor，
 * 则根据其是否存在接口调用Java动态代理或者Cglib进行处理，生成对应的动态代理对象
 * Created by MiaoZhuang on 2016/6/2.
 */
public class AspectJAwareAutoProxyCreater implements BeanPostProcessor, BeanFactoryAware {


    AutoWireCapableBeanFactory autoWireCapableBeanFactory;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {

        //如果生成的bean为通知，直接通过
        if (bean instanceof MethodInterceptor) {
            return bean;
        }
        //如果生成的bean是切面，直接通过
        if (bean instanceof AspectJExpressionPointcutAdvisor) {
            return bean;
        }

        if (bean.getClass().getInterfaces().length == 0) {
            //获取所有的advisor，与当前bean进行匹配
            List<Object> list = autoWireCapableBeanFactory.getBeanByType(AspectJExpressionPointcutAdvisor.class);
            for (Object object : list) {
                AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = (AspectJExpressionPointcutAdvisor) object;
                if (aspectJExpressionPointcutAdvisor.getAspectJExpressionPointcut().matches(bean.getClass())) {
                    TargetSource targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
                    AdvisedSupport advisedSupport = new AdvisedSupport(targetSource, aspectJExpressionPointcutAdvisor.getMethodInterceptor(), aspectJExpressionPointcutAdvisor.getAspectJExpressionPointcut().getMethodMatcher());
                    CglibAopProxy cglibAopProxy = new CglibAopProxy(advisedSupport);
                    return cglibAopProxy.getProxy();
                }
            }
        } else {
            List<Object> list = autoWireCapableBeanFactory.getBeanByType(AspectJExpressionPointcutAdvisor.class);
            for (Object object : list) {
                AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor = (AspectJExpressionPointcutAdvisor) object;
                if (aspectJExpressionPointcutAdvisor.getAspectJExpressionPointcut().matches(bean.getClass())) {
                    TargetSource targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
                    AdvisedSupport advisedSupport = new AdvisedSupport(targetSource, aspectJExpressionPointcutAdvisor.getMethodInterceptor(), aspectJExpressionPointcutAdvisor.getAspectJExpressionPointcut().getMethodMatcher());
                    JdkDynamicAopProxy jdkDynamicAopProxy = new JdkDynamicAopProxy(advisedSupport);
                    return jdkDynamicAopProxy.getProxy();
                }
            }
        }
        return bean;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.autoWireCapableBeanFactory = (AutoWireCapableBeanFactory) beanFactory;
    }

}
