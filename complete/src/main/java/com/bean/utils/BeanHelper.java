package com.bean.utils;


import com.bean.Devbankcashflow;

import java.lang.reflect.Field;

/**
 * @ClassName BeanHelper
 * @Author mjwang
 * @Date 2021/4/26 13:50
 * @Description BeanHelper
 * @Version 1.0
 */
public class BeanHelper {

    public static void main(String[] args) {
        System.out.println(getSqlByClass(Devbankcashflow.class));
    }


    /**
     * 生成sql
     * @param pclass
     * @return
     */
    public static String getSqlByClass(Class<?> pclass){
        String[] fields  = getClassDeclaredFieldNames(pclass);
        String className = pclass.getSimpleName();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("insert into "+ className +"(");
        for (int i = 0; i < fields.length; i++) {
            stringBuffer.append(fields[i]);
            if(i != (fields.length-1)){
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(") VALUES (");
        for (int i = 0; i < fields.length; i++) {
            stringBuffer.append(":"+fields[i]);
            if(i != (fields.length-1)){
                stringBuffer.append(",");
            }
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
    /**
     * 获取类属性名
     *
     * @param pclass
     * @return String[]
     *
     */
    public static String[] getClassDeclaredFieldNames(Class<?> pclass){
        Field[] propertyField;
        String[] returnArray;
        int count = 0;

        if (pclass == null) {
            return null;
        }
        propertyField = pclass.getDeclaredFields();
        if (propertyField != null && propertyField.length > 0) {
            count = propertyField.length;
        }

        returnArray = new String[count];
        for (int i = 0; i < count; i++) {
            returnArray[i] = propertyField[i].getName();
        }
        return returnArray;
    }




}
