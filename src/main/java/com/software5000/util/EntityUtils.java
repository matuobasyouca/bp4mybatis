package com.software5000.util;

import com.zscp.master.util.ArrayUtil;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 提供实体所需的一些快捷操作
 *
 * @author matuobasyouca@gmail.com
 */
public class EntityUtils {

    public static List<Field> getEntityFields(String... fieldNames) {
        if(!ValidUtil.valid(fieldNames)) {
            return null;
        }

        List<Field> fields = new ArrayList<>(fieldNames.length);
        for (String field : fieldNames) {
//            fields.add(ClassUtil.getField(field,true))
        }

        return fields;

    }
}
