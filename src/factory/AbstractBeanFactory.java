package factory;

import application_context.BeanPostProcessor;
import bean_definition.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by MiaoZhuang on 2016/5/25.
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    //存储BeanName与BeanDefinition的键值对
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //存储后置处理器
    protected List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    public Object getBean(String beanDefinitionName) {

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanDefinitionName);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("bean名称" + beanDefinitionName + "未被定义");
        }

        Object bean = beanDefinition.getBeanDefinitionObject();
        if (bean == null) {
            bean = doCreateBean(beanDefinitionMap.get(beanDefinitionName));
            //遍历所有beanDefinitionMap中的切面，如果如当前的bean符合，则将其替换为当前bean的代理
            bean = initializeBean(bean, beanDefinitionName);
            beanDefinition.setBeanDefinitionObject(bean);
        }
        return bean;
    }

    /**
     * 调用后置处理器对获取的bean进行处理
     *
     * @param bean
     * @param beanDefinitionName
     * @return
     */
    public Object initializeBean(Object bean, String beanDefinitionName) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanDefinitionName);
        }
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanDefinitionName);
        }

        return bean;
    }

    /**
     * 获取指定类型的bean集合
     *
     * @param requireType
     * @return
     */
    public List<Object> getBeanByType(Class requireType) {
        List<Object> list = new ArrayList<>();
        for (String beanDefinitionName : beanDefinitionMap.keySet()) {
            if (requireType.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanDefinitionClass())) {
                list.add(this.getBean(beanDefinitionName));
            }
        }
        return list;
    }

    public void addBeanPostProfessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    public void registerBeanDefinition(String beanDefinitionName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanDefinitionName, beanDefinition);
    }

    public abstract Object doCreateBean(BeanDefinition beanDefinition);
}
