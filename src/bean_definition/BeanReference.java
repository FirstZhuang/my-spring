package bean_definition;

/**
 * 用于保存xml中的引用，以便于将bean注入到bean之中
 * Created by MiaoZhuang on 2016/5/26.
 */
public class BeanReference {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
