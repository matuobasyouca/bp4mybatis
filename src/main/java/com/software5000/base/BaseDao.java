package com.software5000.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;
import com.zscp.master.util.collection.CollectionUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * mybatis 接口简单功能封装
 */
@Repository("baseDao")
public class BaseDao extends SqlSessionDaoSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1568331066582893008L;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    /**
     * 根据sql方法名称和对象插入数据库
     */
    public Object insert(String sqlName, Object obj) throws SQLException {
        return getSqlSession().insert(sqlName, obj);

    }

    /**
     * 根据sql方法名称和对象修改数据库
     */
    public int update(String sqlName, Object obj) throws SQLException {
        return getSqlSession().update(sqlName, obj);
    }

    /**
     * 将批量数据插入数据库
     */
    public void insertList(String sqlName, List<?> list) throws SQLException {
        getSqlSession().insert(sqlName, list);
    }

    /**
     * 将批量数据更新到数据库中
     */
    public void updateList(String sqlName, List<?> list) throws SQLException {
        getSqlSession().update(sqlName, list);
    }

    /**
     * 根据sql方法名称和对象id
     */
    public void delete(String sqlName, Object id) throws SQLException {
        getSqlSession().delete(sqlName, id);
    }

    /**
     * 根据sql方法名称和对象id
     */
    public void delete(String sqlName, Object[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Object id = ids[i];
            getSqlSession().delete(sqlName, id);
        }
    }

    /**
     * 根据sql方法名称和对象id
     *
     * @param sqlName sql方法名称
     * @param ids     ids对象列表
     * @return void
     * @throws SQLException
     */
    public void delete(String sqlName, List<?> ids) throws SQLException {
        getSqlSession().delete(sqlName, ids);
    }

    /**
     * 根据sql方法名称和条件，取回查询结果列象
     */
    public List<?> selectList(String sqlName, Object obj) throws SQLException {
        return getSqlSession().selectList(sqlName, obj);
    }

    /**
     * 根据sql方法名称和条件，取回查询结果列象
     */
    public PageInfo selectListByPage(String sqlName, Object obj, Integer startPage, Integer pageSize, String orderBy) throws SQLException {
        return selectListByPage(sqlName, obj, startPage, pageSize, orderBy, false);
    }

    /**
     * 根据sql方法名称和条件，取回查询结果列象
     */
    public PageInfo selectListByPage(String sqlName, Object obj, Integer startPage, Integer pageSize, String orderBy, boolean ignorePermission) throws SQLException {
        if (ignorePermission) {
//            PermissionHelper.ignorePermissionThisTimeBegin();
        }
        PageHelper.startPage(startPage, pageSize, orderBy);
        PageInfo pageInfo = new PageInfo(getSqlSession().selectList(sqlName, obj));
        if (ignorePermission) {
//            PermissionHelper.ignorePermissionThisTimeEnd();
        }
        return pageInfo;
    }

    /**
     * 根据sql方法名称取回查询结果列表
     */
    public List<?> selectList(String sqlName) throws SQLException {
        return getSqlSession().selectList(sqlName);
    }


    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public PageInfo selectEntityByPage(BaseEntity entity, Integer startPage, Integer pageSize, String orderBy) throws SQLException {
        return selectEntityByPage(entity, startPage, pageSize, orderBy, false);
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public PageInfo selectEntityByPage(BaseEntity entity, Integer startPage, Integer pageSize, String orderBy, Boolean ignorePermission) throws SQLException {
        if (ignorePermission) {
//            PermissionHelper.ignorePermissionThisTimeBegin();
        }
        PageHelper.startPage(startPage, pageSize, orderBy);
        PageInfo pageInfo = new PageInfo(this.selectEntity(entity));
        if (ignorePermission) {
//            PermissionHelper.ignorePermissionThisTimeEnd();
        }
        return pageInfo;
    }

    /**
     * 根据sql方法名称和条件，取回单个查询结果对象
     */
    public Object selectObject(String sqlName, Object obj) throws SQLException {
        return getSqlSession().selectOne(sqlName, obj);

    }

    /**
     * 根据sql方法名称，取回单个查询结果对象
     */
    public Object selectObject(String sqlName) throws SQLException {
        return getSqlSession().selectOne(sqlName);
    }

    /**
     * 模仿PreparedStatement防止sql注入
     *
     * @param str
     * @return
     */
    private String transactSQLInjection(Object str) {
        if (str == null) {
            return "null";
        }
        return "'" + str.toString().trim().replace("'", "").replace("\\", "\\\\").replace(";", "；") + "'";
    }

    public <T extends BaseEntity> void deleteEntitys(List<T> entitys) throws SQLException {
        if (entitys.size() < 1) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        entitys.forEach(entity -> ids.add(entity.getId()));
        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" delete from ");
        sqlString.append(entitys.get(0).getClass().getSimpleName()); //tablename
        sqlString.append(" where ");
        StringBuilder fieldString = new StringBuilder();
        try {
            for (T entity : entitys) {
                if (entity.getId() != null) {
                    fieldString.append(entity.getId().toString());
                    fieldString.append(",");
                } else {
                    throw new SQLException("请输入要删除的数据的id");
                }
            }
        } catch (Exception e) {
            logger.error("删除失败，存在id为空的对象");
            throw new SQLException("can't delete data without criteria.");
        }
        sqlString.append("id in (");
        String s = fieldString.toString();
        sqlString.append(s.substring(0, s.length() - 1));//去掉最后一个逗号
        sqlString.append(")");
        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.deleteEntitys", param);

    }

    /**
     * 简单删除实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends com.software5000.base.BaseEntity> void deleteEntityById(Integer id, Class<T> entity) throws SQLException {

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" delete from ");
        sqlString.append(entity.getSimpleName()); //tablename
        sqlString.append(" where ");

        StringBuilder fieldString = new StringBuilder();
        try {
            if (id != null) {
                fieldString.append("id='");
                fieldString.append(id.toString());
                fieldString.append("'");
            } else {
                throw new SQLException("请输入要删除的数据的id");
            }
        } catch (Exception e) {
            logger.error("processing entity delete error, entity : [" + entity.getClass().getName() + "] field : [id]", e);
        }

        if (fieldString.length() <= 0) {
            throw new SQLException("can't delete data without criteria.");
        }
        sqlString.append(fieldString.toString());

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.deleteEntity", param);
    }


    /**
     * 简单删除实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> void deleteEntity(T entity) throws SQLException {

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" delete from ");
        sqlString.append(entity.getClass().getSimpleName()); //tablename
        sqlString.append(" where ");

        StringBuilder fieldString = new StringBuilder();

        for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), true, NotDatabaseField.class)) {
            try {
                Object value = ClassUtil.getValueByField(entity, fieldName);

                if (!ValidUtil.isEmpty(value)) {
                    if (fieldString.length() != 0) {
                        fieldString.append(" AND ");
                    }
                    fieldString.append(fieldName + "=");  // fieldname
                    fieldString.append(transactSQLInjection(value.toString()));
                    fieldString.append(" ");
                }
            } catch (Exception e) {
                logger.error("processing entity delete error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
                sqlString.append("null");
            }
        }

        if (fieldString.length() <= 0) {
            throw new SQLException("can't delete data without criteria.");
        }
        sqlString.append(fieldString.toString());

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.deleteEntity", param);
    }


    /**
     * 简单更新实体对象，根据ID更新, 更新所有的字段内容，包括值为null和为空的都会更新，慎用！
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> void updateEntityAllValue(T entity) throws SQLException {
        if (entity == null || entity.getId() == null || entity.getId() <= 0) {
            throw new SQLException("can't update data without  -> id <-.");
        }

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" update ");
        sqlString.append(entity.getClass().getSimpleName()); // tablename
        sqlString.append(" set ");

        StringBuilder fieldString = new StringBuilder();
        for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), false, NotDatabaseField.class)) {
            if (fieldString.length() != 0) {
                fieldString.append(",");
            }
            try {
                Object value = ClassUtil.getValueByField(entity, fieldName);
                if (ValidUtil.isEmpty(value)) {
                    fieldString.append(fieldName); // fieldname
                    fieldString.append("=null");
                } else {
                    fieldString.append(fieldName); // fieldname
                    fieldString.append("= ");
                    fieldString.append(transactSQLInjection(value.toString()));
                    fieldString.append("  ");
                }
            } catch (Exception e) {
                logger.error("processing entity update error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
                sqlString.append("null");
            }
        }
        sqlString.append(fieldString.toString());

        sqlString.append(" where ");

        // 过滤条件只能为ID
        sqlString.append(" id='");
        sqlString.append(entity.getId());
        sqlString.append("' ");

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.updateEntity", param);
    }

    /**
     * 更新实体对象，根据ID更新，只更新有内容的字段,isSupportBlank为true表示[空值也会更新只有null才不会更新]，false表示[空值跟null都不会更新]
     *
     * @param entity
     * @param isSupportBlank 是否支持空值的更新
     * @throws SQLException
     */
    public void updateEntityOnlyHaveValue(BaseEntity entity, boolean isSupportBlank) throws SQLException {
        if (entity == null || entity.getId() == null || entity.getId() <= 0) {
            throw new SQLException("can't update data without  -> id <-.");
        }

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" update ");
        sqlString.append(entity.getClass().getSimpleName()); //tablename
        sqlString.append(" set ");

        StringBuilder fieldString = new StringBuilder();
        for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), false, NotDatabaseField.class)) {
            try {
                Object value = ClassUtil.getValueByField(entity, fieldName);
                if ((isSupportBlank ? value != null : ValidUtil.isNotEmpty(value)) && !"id".equals(fieldName)) {
                    if (fieldString.length() != 0) {
                        fieldString.append(",");
                    }
                    fieldString.append(fieldName);  // fieldname
                    fieldString.append("= ");
                    fieldString.append(transactSQLInjection(value.toString()));
                    fieldString.append(" ");
                }
            } catch (Exception e) {
                logger.error("processing entity update error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
//                sqlString.append("null");
            }
        }
        sqlString.append(fieldString.toString());
        if (ValidUtil.isEmpty(fieldString.toString())) {
            return;
        }

        sqlString.append(" where ");

        // 过滤条件只能为ID
        sqlString.append(" id='");
        sqlString.append(entity.getId());
        sqlString.append("' ");

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.updateEntity", param);
    }

    public void updateEntityList(List<? extends BaseEntity> entitys) throws SQLException {
        updateEntityListOnlyHaveValue(entitys, false);
    }

    public void updateEntityListOnlyHaveValue(List<? extends BaseEntity> entitys, boolean isSupportBlank) throws SQLException {

        if (entitys == null || entitys.size() <= 0) {
            return;
        }
        if (entitys.size() == 1) {
            updateEntityOnlyHaveValue(entitys.get(0), isSupportBlank);
            return;
        }
        StringBuilder sqlString = new StringBuilder();
        for (BaseEntity entity : entitys) {
            sqlString.append("update ");
            sqlString.append(entity.getClass().getSimpleName()); //tablename
            sqlString.append(" set ");

            StringBuilder fieldString = new StringBuilder();
            for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), false, NotDatabaseField.class)) {
                try {
                    Object value = ClassUtil.getValueByField(entity, fieldName);
                    if ((isSupportBlank ? value != null : ValidUtil.isNotEmpty(value)) && !"id".equals(fieldName)) {
                        if (fieldString.length() != 0) {
                            fieldString.append(",");
                        }
                        fieldString.append(fieldName);  // fieldname
                        fieldString.append("= ");
                        fieldString.append(transactSQLInjection(value.toString()));
                        fieldString.append(" ");
                    }
                } catch (Exception e) {
                    logger.error("processing entity update error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
                }
            }
            sqlString.append(fieldString.toString());
            if (ValidUtil.isEmpty(fieldString.toString())) {
                return;
            }

            sqlString.append(" where ");

            // 过滤条件只能为ID
            sqlString.append(" id='");
            sqlString.append(entity.getId());
            sqlString.append("'; ");
        }

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.updateEntity", param);
    }


    /**
     * 更新实体对象，根据ID更新，只更新有内容的字段,
     * 除非将该字段放入到fieldNames中，isSupportBlankAndNull为true表示[在fieldNames中的字段空值跟null都会更新]，false表示[在fieldNames中的字段只会更新空值，null不会更新空值]
     *
     * @param entity
     * @param fieldNames            字段
     * @param isSupportBlankAndNull
     * @throws SQLException
     */
    public void updateEntityOnlyHaveValueAndNull(BaseEntity entity, List<String> fieldNames, boolean isSupportBlankAndNull) throws SQLException {
        if (entity == null || entity.getId() == null || entity.getId() <= 0) {
            throw new SQLException("can't update data without  -> id <-.");
        }

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" update ");
        sqlString.append(entity.getClass().getSimpleName()); //tablename
        sqlString.append(" set ");

        StringBuilder fieldString = new StringBuilder();
        for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), false, NotDatabaseField.class)) {
            try {
                Object value = ClassUtil.getValueByField(entity, fieldName);
                if ((fieldNames.contains(fieldName) ? (isSupportBlankAndNull || value != null) : ValidUtil.isNotEmpty(value)) && !"id".equals(fieldName)) {
                    if (fieldString.length() != 0) {
                        fieldString.append(",");
                    }
                    fieldString.append(fieldName);  // fieldname
                    fieldString.append("= ");

                    fieldString.append(transactSQLInjection(value));
                    fieldString.append(" ");
                }
            } catch (Exception e) {
                logger.error("processing entity update error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
//                sqlString.append("null");
            }
        }
        sqlString.append(fieldString.toString());
        if (ValidUtil.isEmpty(fieldString.toString())) {
            return;
        }

        sqlString.append(" where ");

        // 过滤条件只能为ID
        sqlString.append(" id='");
        sqlString.append(entity.getId());
        sqlString.append("' ");

        Map<String, String> param = new HashMap<String, String>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.updateEntity", param);
    }


    /**
     * 简单更新实体对象，根据ID更新，只更新有内容的字段，不更新null跟空的字段
     *
     * @param entity
     * @throws SQLException
     */
    public void updateEntity(BaseEntity entity) throws SQLException {
        updateEntityOnlyHaveValue(entity, false);
    }

    /**
     * 简单插入实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> T insertEntity(T entity) throws SQLException {
        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" insert into ");
        sqlString.append(entity.getClass().getSimpleName()); //tablename
        sqlString.append(" (");
        sqlString.append(ClassUtil.getColumnNamesAsString(entity.getClass(), true, NotDatabaseField.class));  // fieldname
        sqlString.append(") values (");
        entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
        entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        sqlString.append(Arrays.stream(ClassUtil.getColumnNames(entity.getClass(), true, NotDatabaseField.class)).map(t -> transactSQLInjection(ClassUtil.getValueByField(entity, t))).reduce((a, b) -> a + "," + b).get());
        sqlString.append(") ");

        Map<String, Object> param = new HashMap<>();
        param.put("baseSql", sqlString.toString());
        this.insert("BaseDao.insertEntity", param);
        entity.setId(Integer.parseInt(param.get("id").toString()));
        return entity;
    }

    /**
     * 简单插入实体对象
     *
     * @param entitys
     * @throws SQLException
     */
    public List insertEntityList(List<? extends BaseEntity> entitys) throws SQLException {
        if (entitys == null || entitys.size() == 0) {
            return null;
        }
        if (entitys.size() == 1) {
            List insertEntityList = new ArrayList<>();
            insertEntityList.add(this.insertEntity(entitys.get(0)));
            return insertEntityList;
        }

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" insert into ");
        sqlString.append(entitys.get(0).getClass().getSimpleName()); //tablename
        sqlString.append(" (");
        sqlString.append(ClassUtil.getColumnNamesAsString(entitys.get(0).getClass(), true, NotDatabaseField.class));  // fieldname
        sqlString.append(") values ");

        try {
            Timestamp nowTime = new Timestamp(System.currentTimeMillis());
            List collect = entitys.stream().peek(entity -> {
                entity.setCreateTime(nowTime);
                entity.setUpdateTime(nowTime);
            }).collect(Collectors.toList());
            sqlString.append(collect.stream().map(entity -> Arrays.stream(ClassUtil.getColumnNames(entity.getClass(), true, NotDatabaseField.class)).map(t -> transactSQLInjection(ClassUtil.getValueByField(entity, t))).reduce((a, b) -> a + "," + b).get()).reduce((a, b) -> (a.toString().startsWith("(") ? a : "(" + a + ")") + "," + "(" + b + ")").get());
        } catch (Exception e) {
            logger.error("processing entity list insert error, entity : [" + entitys.get(0).getClass().getName() + "] field : [id]", e);
        }

        Map<String, Object> param = new HashMap<>();
        param.put("baseSql", sqlString);
        param.put("list", entitys);
        this.insert("BaseDao.insertEntityList", param);
        return entitys;
    }

    /**
     * 大数据量的插入,需分割插入
     *
     * @param entitys
     * @return
     */
    public List insertLangEntityList(List<? extends BaseEntity> entitys) throws SQLException {
        List list = new ArrayList();
        int size = entitys.size();
        if (size < 10000) {
            List entryList = insertEntityList(entitys);
            if (entryList != null && entryList.size() > 0) {
                list.addAll(entryList);
            }
        } else {
            List<? extends List<? extends BaseEntity>> lists = CollectionUtil.splitList(entitys, 10000);
            for (List<? extends BaseEntity> baseEntities : lists) {
                List entryList = insertEntityList(baseEntities);
                if (entryList != null && entryList.size() > 0) {
                    list.addAll(entryList);
                }
            }
        }
        return list;
    }

    /**
     * 简单方法返回单个对象
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T extends BaseEntity> T selectSingleEntity(T entity) {
        try {
            return this.selectEntity(entity).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> selectEntity(T entity) throws SQLException {

        return selectEntity(entity, null);
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> selectEntity(T entity, String ordreBy) throws SQLException {

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" select ");
        sqlString.append(ClassUtil.getColumnNamesAsString(entity.getClass(), true, NotDatabaseField.class));  // fieldname
        sqlString.append(" from ");
        sqlString.append(entity.getClass().getSimpleName()); //tablename
        sqlString.append(" where 1=1 ");

        StringBuilder fieldString = new StringBuilder();

        for (String fieldName : ClassUtil.getColumnNames(entity.getClass(), true, NotDatabaseField.class)) {
            try {
                // 因为creatTime由BaseEntity负责生成所以默认会带值
                if ("createTime".equals(fieldName)) {
                    continue;
                }
                Object value = ClassUtil.getValueByField(entity, fieldName);

                if (!ValidUtil.isEmpty(value)) {
                    fieldString.append(" AND ");
                    fieldString.append(fieldName + "=");  // fieldname
                    fieldString.append(transactSQLInjection(value.toString()));
                    fieldString.append(" ");
                }
            } catch (Exception e) {
                logger.error("processing entity select error, entity : [" + entity.getClass().getName() + "] field : [" + fieldName + "]", e);
            }
        }

        sqlString.append(fieldString.toString());
        if (!ValidUtil.isEmpty(ordreBy)) {
            sqlString.append(" ORDER BY ");
            sqlString.append(ordreBy);
        }

        Map<String, String> param = new HashMap<>();
        param.put("baseSql", sqlString.toString());
        List lastResult = this.selectList("BaseDao.selectEntity", param);
        return lastResult;
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T selectEntityById(Integer entityId, Class<T> entity) throws SQLException {

        StringBuilder sqlString = new StringBuilder();
        sqlString.append(" select ");
        sqlString.append(ClassUtil.getColumnNamesAsString(entity, true, NotDatabaseField.class));  // fieldname
        sqlString.append(" from ");
        sqlString.append(entity.getSimpleName()); //tablename
        sqlString.append(" where id= ");
        sqlString.append(entityId);

        Map<String, String> param = new HashMap<>();
        param.put("baseSql", sqlString.toString());
        List lastResult = this.selectList("BaseDao.selectEntity", param);
        List<?> result = lastResult;
        List<BaseEntity> tempList = new ArrayList<>();
        for (Object sr : result) {
            try {
                BaseEntity singleResult = (BaseEntity) Class.forName(entity.getName()).newInstance();

                for (String key : ((Map<String, Object>) sr).keySet()) {
                    ClassUtil.setValueByField(singleResult, key, ((Map<String, Object>) sr).get(key));
                }

                tempList.add(singleResult);
            } catch (Exception e) {
                logger.error("processing entity setter value error, entity : [" + entity.getClass().getName() + "] ", e);
            }
        }
        lastResult = tempList;
//        if(lastResult.size()<0)
        return lastResult.size() <= 0 ? null : (T) lastResult.get(0);
    }
}
