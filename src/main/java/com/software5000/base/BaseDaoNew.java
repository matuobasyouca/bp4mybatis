package com.software5000.base;

import com.software5000.base.jsql.AndExpressionList;
import com.software5000.util.JsqlUtils;
import com.zscp.master.util.ArrayUtil;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

/**
 * @author matuobasyouca@gmail.com
 */
@Repository("baseDaoNew")
public class BaseDaoNew extends SqlSessionDaoSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    // region insert 方法块

    /**
     * 根据sql方法名称和对象插入数据库
     *
     * @param sqlName xml中的sql id
     * @param obj     传入操作对象
     * @return 影响行数
     */
    public int insert(String sqlName, Object obj) {
        return getSqlSession().insert(sqlName, obj);

    }

    /**
     * 简单插入实体对象
     *
     * @param entity 实体对象
     * @return 带id的插入对象
     */
    public <T extends BaseEntity> T insertEntity(T entity) {
        Insert insert = new Insert();
        insert.setTable(new Table(entity.getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getAllColumnNamesFromEntity(entity.getClass()));
        insert.setItemsList(JsqlUtils.getAllColumnValueFromEntity(entity, insert.getColumns()));

        Map<String, Object> param = new HashMap<>(2);
        param.put("baseSql", insert.toString());
        param.put("entity", entity);
        this.insert("BaseDao.insertEntity", param);

        return entity;
    }


    /**
     * 简单批量插入实体对象
     *
     * @param entities 待插入的实体列表
     * @return 带id的插入对象列表
     */
    public List insertEntityList(List<? extends BaseEntity> entities) {
        if (entities == null || entities.size() == 0) {
            return null;
        }

        Insert insert = new Insert();
        insert.setTable(new Table(entities.get(0).getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getAllColumnNamesFromEntity(entities.get(0).getClass()));
        MultiExpressionList multiExpressionList = new MultiExpressionList();
        entities.stream().map(e -> JsqlUtils.getAllColumnValueFromEntity(e, insert.getColumns())).forEach(multiExpressionList::addExpressionList);
        insert.setItemsList(multiExpressionList);

        Map<String, Object> param = new MapperMethod.ParamMap<>();
        param.put("baseSql", insert.toString());
        param.put("list", entities);
        this.insert("BaseDao.insertEntityList", param);
        return entities;
    }
    // endregion

    // region delete 方法块

    /**
     * 根据sql方法名称和对象id
     *
     * @param sqlName xml中的sql id
     * @param obj     传入操作对象
     * @return 影响行数
     */
    public int delete(String sqlName, Object obj) {
        return getSqlSession().delete(sqlName, obj);
    }


    /**
     * 简单删除实体对象
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    public <T extends BaseEntity> int deleteEntity(T entity) {
        return this.deleteEntity(entity.getId(), entity.getClass());
    }

    /**
     * 简单删除实体对象
     *
     * @param entityClass 实体对象的class
     * @param id          待删除的id
     * @return 影响行数
     */
    public <T extends BaseEntity> int deleteEntity(Integer id, Class<T> entityClass) {
        Delete delete = new Delete();
        delete.setTable(new Table(entityClass.getSimpleName()));
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column("id"));
        equalsTo.setRightExpression(new LongValue(id));
        delete.setWhere(equalsTo);

        Map<String, Object> param = new HashMap<>(1);
        param.put("baseSql", delete.toString());
        return this.delete("BaseDao.deleteEntity", param);
    }
    // endregion


    // region update 方法块

    /**
     * 根据sql方法名称和对象修改数据库
     *
     * @param sqlName xml中的sql id
     * @param obj     传入操作对象
     * @return 影响行数
     */
    public int update(String sqlName, Object obj) {
        return getSqlSession().update(sqlName, obj);
    }

    /**
     * 简单更新实体对象
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    public int updateEntity(BaseEntity entity) throws SQLException {
        return updateEntityOnlyHaveValue(entity, true);
    }

    /**
     * 更新实体对象，根据ID更新，只更新有内容的字段
     *
     * @param entity
     * @param isSupportBlank 是否支持空值的更新 为true表示[空值也会更新只有null才不会更新]，false表示[空值跟null都不会更新]
     * @return 影响行数
     * @throws SQLException
     */
    public int updateEntityOnlyHaveValue(BaseEntity entity, boolean isSupportBlank) throws SQLException {
        if (entity == null || entity.getId() == null || entity.getId() <= 0) {
            throw new SQLException("can't update data without value of -> id <-.");
        }

        return this.updateEntityWithNamedColumns(
                JsqlUtils.getAllColumnNamesFromEntityExceptSome(
                        entity.getClass(),
                        Arrays.asList(new String[]{"id"})
                ).toArray(new Column[]{}),
                new Column[]{new Column("id")},
                entity,
                true
        );

    }

    /**
     * 根据指定字段设置过滤条件，指定字段更新值
     *
     * @param valueCols      指定的更新值的字段
     * @param conditionCols  指定的条件列字段
     * @param entity         待更新实体
     * @param isSupportBlank 是否支持空值的更新 为true表示[空值也会更新只有null才不会更新]，false表示[空值跟null都不会更新]
     * @return 影响行数
     * @throws SQLException
     */
    public int updateEntityWithNamedColumns(Column[] valueCols, Column[] conditionCols, BaseEntity entity, boolean isSupportBlank) throws SQLException {
        if (!ValidUtil.valid(conditionCols)) {
            throw new SQLException("can't update data without value of condition columns.");
        }

        Update update = new Update();
        update.setTables(Arrays.asList(new Table(entity.getClass().getSimpleName())));
        Object[] colsAndValuesForValues = JsqlUtils.getNamedColumnAndValueFromEntity(entity, valueCols, isSupportBlank,false);
        update.setColumns((List<Column>) colsAndValuesForValues[0]);
        update.setExpressions((List<Expression>) colsAndValuesForValues[1]);

        AndExpressionList andExpressionList = new AndExpressionList();
        Arrays.stream(conditionCols)
                .forEach(e -> andExpressionList.append(JsqlUtils.equalTo(e, JsqlUtils.getColumnValueFromEntity(entity, e.getColumnName()))));

        update.setWhere(andExpressionList.get());

        return this.update("BaseDao.updateEntity", new HashMap<String, String>() {{
            put("baseSql", update.toString());
        }});
    }
    // endregion

    // region select 方法块

    /**
     * 根据sql方法名称取回查询结果列表
     */
    public List<?> selectList(String sqlName) throws SQLException {
        return getSqlSession().selectList(sqlName);
    }

    /**
     * 根据sql方法名称和条件，取回查询结果列象
     */
    public List<?> selectList(String sqlName, Object obj) throws SQLException {
        return getSqlSession().selectList(sqlName, obj);
    }


    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> List<T> selectEntity(T entity) throws SQLException {
        return selectEntity(entity, null);
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> List<T> selectEntity(T entity, String ordreBy) throws SQLException {
        PlainSelect plainSelect = new PlainSelect();
        plainSelect.setIntoTables(Arrays.asList(new Table(entity.getClass().getSimpleName())));
//        select

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
                    fieldString.append((value.toString()));
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
        List lastResult = (List) this.selectList("BaseDao.selectEntity", param);
        List<?> result = null;
//        if (lastResult instanceof Page) {
//            result = ((Page) lastResult).getResult();
//        } else {
//            result = lastResult;
//        }
        List<BaseEntity> tempList = new ArrayList<>();
        for (Object sr : result) {
            try {
                BaseEntity singleResult = (BaseEntity) Class.forName(entity.getClass().getName()).newInstance();

                for (String key : ((Map<String, Object>) sr).keySet()) {
                    ClassUtil.setValueByField(singleResult, key, ((Map<String, Object>) sr).get(key));
                }

                tempList.add(singleResult);
            } catch (Exception e) {
                logger.error("processing entity setter value error, entity : [" + entity.getClass().getName() + "] ", e);
            }
        }
//        if (lastResult instanceof Page) {
//            ((Page) lastResult).getResult().clear();
//            ((Page) lastResult).getResult().addAll(tempList);
//        } else {
//            lastResult = tempList;
//        }
        return lastResult;
    }

    // endregion
}
