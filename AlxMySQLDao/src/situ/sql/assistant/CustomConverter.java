package situ.sql.assistant;

/**
 * Created by Administrator on 2017/8/14.
 */
public interface CustomConverter {
    /**
     *
     * @param columnName 数据库中的列名
     * @param value 数据库取出的值
     * @return bean中的字段名，该字段的值
     */
    ConverterPackage convert(String columnName, Object value);
}