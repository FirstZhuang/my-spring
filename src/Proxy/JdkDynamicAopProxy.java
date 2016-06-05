package Proxy;

import Proxy_Factory.AdvisedSupport;
import aopalliance.ReflectiveMethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by MiaoZhuang on 2016/6/1.
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advisedSupport;

    public JdkDynamicAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(advisedSupport.getTargetSource().getTarget().getClass().getClassLoader(), advisedSupport.getTargetSource().getTargetInterfaces(), this);
    }

    /**
     * 所实现的回调函数，若该对象调用的method与表达式匹配，则代理，否则不代理，执行原来的函数
     *
     * @param object 生成的代理对象
     * @param method 原对象的方法“指针”
     * @param args   方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        if (advisedSupport.getMethodMatcher().matches(method, object.getClass())) {
            System.out.println(object.getClass().getName());
            MethodInterceptor methodInterceptor = advisedSupport.getMethodInterceptor();
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advisedSupport.getTargetSource().getTarget(), method, args));
        } else {
            return method.invoke(advisedSupport.getTargetSource().getTarget(), args);
        }
    }
}
