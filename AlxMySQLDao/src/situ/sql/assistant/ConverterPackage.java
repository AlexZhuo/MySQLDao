package situ.sql.assistant;

/**
 * Created by Administrator on 2017/8/14.
 */
public class ConverterPackage {
    public String propertyName;//Bean中的字段名，会通过这个调用其set方法
    public String propertyClass;//set方法中参数的class，如java.lang.String
    public Object val;//该字段的值

    public ConverterPackage(String propertyName, String propertyClass, Object val) {
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.val = val;
    }
}
