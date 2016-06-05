package bean_definition_reader;

import resource.Resource;

/**
 * BeanDefinitionReader顶层接口，定义了载入Resource的方法
 * Created by MiaoZhuang on 2016/5/25.
 */
public interface BeanDefinitionReader {

    void loadBeanDefinitions(Resource resource);

}
