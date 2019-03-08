package com.software5000.base;

import com.software5000.util.JsqlUtils;
import com.zscp.master.util.ClassUtil;
import com.zscp.master.util.ValidUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matuobasyouca@gmail.com
 */
@Repository("baseDaoNew")
public class BaseDaoNew  extends SqlSessionDaoSupport {

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
     * @param obj 传入操作对象
     *
     * @return 影响行数
     */
    public int insert(String sqlName, Object obj) {
        return getSqlSession().insert(sqlName, obj);

    }

    /**
     * 简单插入实体对象
     *
     * @param entity 实体对象
     *
     * @return 带id的插入对象
     */
    public <T extends BaseEntity> T insertEntity(T entity) {
        Insert insert = new Insert();
        insert.setTable(new Table(entity.getClass().getSimpleName()));
        insert.setColumns(JsqlUtils.getAllColumnNamesFromEntity(entity.getClass()));
        insert.setItemsList(JsqlUtils.getAllColumnValueFromEntity(entity,insert.getColumns()));

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
     *
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
        entities.stream().map(e -> JsqlUtils.getAllColumnValueFromEntity(e,insert.getColumns())).forEach(multiExpressionList::addExpressionList);
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
     * @param obj 传入操作对象
     *
     * @return 影响行数
     */
    public int delete(String sqlName, Object obj) {
        return getSqlSession().delete(sqlName, obj);
    }


    /**
     * 简单删除实体对象
     *
     * @param entity 实体对象
     *
     * @return 影响行数
     */
    public <T extends BaseEntity> int deleteEntity(T entity) {
        return this.deleteEntity(entity.getId(),entity.getClass());
    }

    /**
     * 简单删除实体对象
     *
     * @param entityClass 实体对象的class
     * @param id 待删除的id
     *
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
     * @param obj 传入操作对象
     *
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
     *
     * @throws SQLException
     *
     * @return 影响行数
     */
    public int updateEntityOnlyHaveValue(BaseEntity entity, boolean isSupportBlank) throws SQLException {
        if (entity == null || entity.getId() == null || entity.getId() <= 0) {
            throw new SQLException("can't update data without value of -> id <-.");
        }

        Update update = new Update();
        update.setTables(Arrays.asList(new Table(entity.getClass().getSimpleName())));
        Object[] colsAndValues = JsqlUtils.getNotEmptyColumnAndValueFromEntity(entity, isSupportBlank);
        update.setColumns((List<Column>) colsAndValues[0]);
        update.setExpressions((List<Expression>) colsAndValues[1]);
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column("id"));
        equalsTo.setRightExpression(new LongValue(entity.getId()));
        update.setWhere(equalsTo);

        Map<String, String> param = new HashMap<>(1);
        param.put("baseSql", update.toString());
        return this.update("BaseDao.updateEntity", param);
    }

    // endregion

    // region select 方法块

    // endregion
}
