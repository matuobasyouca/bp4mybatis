package com.software5000.base;

import com.github.pagehelper.Page;
import com.software5000.base.jsql.AndExpressionList;
import com.software5000.util.JsqlUtils;
import com.sun.istack.internal.NotNull;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
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

    /**
     * 默认的数据库结构为 snake (a_b_c),代码中会将骆驼转换为蛇形
     * 如果数据库结果默认为 camel,则本变量设置为false
     */
    public final static boolean DB_SCHEMES_SNAKE_TYPE = true;

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
        insert.setTable(new Table(JsqlUtils.transCamelToSnake(entity.getClass().getSimpleName())));
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
        insert.setTable(new Table(JsqlUtils.transCamelToSnake(entities.get(0).getClass().getSimpleName())));
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
        delete.setTable(new Table(JsqlUtils.transCamelToSnake(entityClass.getSimpleName())));
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
     * 简单更新实体对象
     *
     * @param entity 实体对象
     * @return 影响行数
     */
    public int updateEntityCustom(BaseEntity entity) throws SQLException {
        //TODO 这里应该是要带指定参数和条件进行更新的方法，待处理
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
        update.setTables(Arrays.asList(new Table(JsqlUtils.transCamelToSnake(entity.getClass().getSimpleName()))));
        Object[] colsAndValuesForValues = JsqlUtils.getNamedColumnAndValueFromEntity(entity, valueCols, isSupportBlank, false);
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
    public <T extends BaseEntity> List<T> selectEntity(T entity) throws SQLException, JSQLParserException {
        return selectEntity(entity, null);
    }

    /**
     * 简单加载实体对象
     *
     * @param entity
     * @throws SQLException
     */
    public <T extends BaseEntity> List<T> selectEntity(T entity, String ordreBy) throws SQLException, JSQLParserException {
        PlainSelect plainSelect = new PlainSelect();
        plainSelect.setSelectItems(Arrays.asList(new AllColumns()));
        plainSelect.setFromItem(new Table(JsqlUtils.transCamelToSnake(entity.getClass().getSimpleName())));
        AndExpressionList andExpressionList = new AndExpressionList();
        Object[] colsAndValues = JsqlUtils.getNamedColumnAndValueFromEntity(entity, null, false, false);

        for (int i = 0; i < ((List) colsAndValues[0]).size(); i++) {
            andExpressionList.append(JsqlUtils.equalTo((Column) ((List) colsAndValues[0]).get(i), (Expression) ((List) colsAndValues[1]).get(i)));
        }

        plainSelect.setWhere(andExpressionList.get());
        plainSelect.setOrderByElements(JsqlUtils.getOrderByElementFromString(ordreBy));

        // 构建Sql并执行
        List lastResult = this.selectList("BaseDao.selectEntity", new HashMap<String, String>() {{
            put("baseSql", plainSelect.toString());
        }});

        // 生成对应对象列表，并且赋值
        return fillEntities(entity, lastResult);
    }


    private <T extends BaseEntity> List<T> fillEntities(T entity, List lastResult) {
        List<?> result = null;
        if (lastResult instanceof Page) {
            result = ((Page) lastResult).getResult();
        } else {
            result = lastResult;
        }
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
        if (lastResult instanceof Page) {
            ((Page) lastResult).getResult().clear();
            ((Page) lastResult).getResult().addAll(tempList);
        } else {
            lastResult = tempList;
        }
        return lastResult;
    }

    // endregion
}
