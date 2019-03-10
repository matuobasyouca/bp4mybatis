package com.software5000.dao;


import com.software5000.base.BaseDao;
import com.software5000.base.BaseDaoNew;
import com.software5000.base.jsql.AndExpressionList;
import com.software5000.biz.entity.SystemCode;
import com.software5000.util.JsqlUtils;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-core.xml", "classpath:spring-db.xml"})
//@Transactional
public class BaseDaoTest {

    private Logger logger = LoggerFactory.getLogger(BaseDaoTest.class);

    @Autowired
    BaseDao baseDao;

    @Autowired
    BaseDaoNew baseDaoNew;

    @Test
    public void testSelectRecChannel() {

        try {

            long t1 = System.currentTimeMillis();
            List<SystemCode> sc = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                SystemCode systemCode = null;
                systemCode = new SystemCode();
                systemCode.setCodeFiter(1);
                systemCode.setCreateTime(new Timestamp(System.currentTimeMillis()));
                systemCode.setCodeName("codename ' and 1=1");

                sc.add(systemCode);
            }

            SystemCode systemCode = null;
            systemCode = new SystemCode();
            systemCode.setId(5);
            systemCode.setCodeName("insert into fxxk your name is '' where 1=1 ; sho");
            systemCode.setUpdateTime(null);
            systemCode.setCreateTime(null);

            String sql = "UPDATE SystemCode SET codeName = 'my name is \\'\\' where 1=1 ; sho' WHERE id = 2";
            Statement parse = CCJSqlParserUtil.parse(sql);

            PlainSelect plainSelect = new PlainSelect();
            plainSelect.setSelectItems(Arrays.asList(new AllColumns()));
            plainSelect.setFromItem(new Table(SystemCode.class.getSimpleName()));
            plainSelect.getWhere();

            plainSelect.setWhere(
                    new AndExpressionList()
                            .append(JsqlUtils.equalTo(new Column("id"), new LongValue(systemCode.getId())))
                            .append(JsqlUtils.equalTo(new Column("codeType"), new NullValue()))
                            .get()
            );


//            Object[] result = JsqlUtils.getNotEmptyColumnAndValueFromEntity(systemCode, true);


//            plainSelect.setIntoTables(Arrays.asList(new Table(SystemCode.class.getSimpleName())));
            logger.info(plainSelect.toString());
            long t2 = System.currentTimeMillis();
            logger.info("============> time is : "+(t2-t1));

            baseDaoNew.updateEntity(systemCode);
//            String s = "UPDATE SystemCode SET codeName = 'fxxk your name is \\'\\' where 1=1 ; sho', updateTime = {ts '2019-03-10 09:24:44.773'} WHERE 1 = 1 AND id = 4 ";
//            baseDaoNew.update("BaseDao.updateEntity", new HashMap<String, String>() {{
//                put("baseSql", s.toString());
//            }});
//            baseDaoNew.deleteEntity(systemCode);
//            sc = baseDaoNew.insertEntityList(sc);
//            systemCode.setId(1);
//            List<SystemCode> result = baseDao.selectEntity(systemCode);
//            logger.info("show me "+ result.size());
            long t3 = System.currentTimeMillis();
            logger.info("============> time is : "+(t3-t2));
        } catch (Exception e) {
            logger.error("query error!", e);
        }
    }
}
