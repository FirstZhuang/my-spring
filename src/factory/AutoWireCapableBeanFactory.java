package factory;

import application_context.BeanFactoryAware;
import bean_definition.BeanDefinition;
import bean_definition.BeanReference;
import bean_definition.PropertyValue;
import bean_definition.PropertyValues;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Created by MiaoZhuang on 2016/5/25.
 */
public class AutoWireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    public Object doCreateBean(BeanDefinition beanDefinition) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition);
            applyPropertyValues(bean, beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 利用Java的反射机制创建指定BeanDefinition的对象
     */
    public Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanDefinitionClass().newInstance();
    }

    /**
     * 使用java的内省机制给bean中的属性赋值
     */
    public void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {
        //对AutoProxyCreater进行赋值
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            if (propertyValue.getObject() instanceof String) {
                Method declaredMethod = bean.getClass().getDeclaredMethod("set" + propertyValue.getName().substring(0, 1).toUpperCase() + propertyValue.getName().substring(1), String.class);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(bean, (String) propertyValue.getObject());
            } else {
                BeanReference beanReference = (BeanReference) propertyValue.getObject();
                String refName = beanReference.getName();
                Object object = getBean(refName);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyValue.getName(), beanDefinition.getBeanDefinitionClass());
                Method method = propertyDescriptor.getWriteMethod();
                method.invoke(bean, beanDefinitionMap.get(refName).getBeanDefinitionClass().cast(object));
            }
        }
    }
}
