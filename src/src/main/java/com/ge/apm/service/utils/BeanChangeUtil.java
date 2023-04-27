package com.ge.apm.service.utils;

import com.ge.apm.domain.hisory.HistoryInfo;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;

import javax.persistence.Column;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 503310146
 */
public class BeanChangeUtil {
    private BeanChangeUtil() {
    }

    public static <T> List<HistoryInfo> contrastObj(T oldBean, T newBean) {
        List<HistoryInfo> list = ListUtil.newArrayList();
        try {
            // 通过反射获取类的类类型及字段属性
            Class<?> clazz = oldBean.getClass();
            List<Field> fields = ListUtil.newArrayList(clazz.getDeclaredFields()).stream().filter(item -> {
                Column annotation = item.getAnnotation(Column.class);
                return annotation != null;
            }).collect(Collectors.toList());
            for (Field field : fields) {
                if ("createdDate".equals(field.getName()) || "lastModifiedDate".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                // 获取对应属性值
                Method getMethod = pd.getReadMethod();
                Object oldValue = getMethod.invoke(oldBean);
                Object newValue = getMethod.invoke(newBean);
                if (!ObjectUtil.equals(oldValue, newValue)) {
                    list.add(new HistoryInfo(field.getName(), oldValue, newValue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void packHistoryObj(List<String> fieldNames, Object historyBean, Object oldBean, Object newBean) {
        try {
            Class<?> beanClazz = oldBean.getClass();
            Class<?> historyClazz = historyBean.getClass();
            for (String fieldName : fieldNames) {
                PropertyDescriptor beanPd = new PropertyDescriptor(fieldName, beanClazz);
                PropertyDescriptor historyPd = new PropertyDescriptor(fieldName, historyClazz);

                Method writeMethod = historyPd.getWriteMethod();

                Method readMethod = beanPd.getReadMethod();
                Object oldValue = readMethod.invoke(oldBean);
                Object newValue = readMethod.invoke(newBean);
                if (!ObjectUtil.equals(oldValue, newValue)) {
                    writeMethod.invoke(historyBean, oldValue + "->" + newValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
