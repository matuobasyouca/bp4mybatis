package com.software5000.dao;


import com.software5000.base.BaseDao;
import com.software5000.base.BaseDaoNew;
import com.software5000.base.jsql.ConditionWrapper;
import com.software5000.biz.entity.SystemCode;
import com.software5000.util.JsqlUtils;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
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
import java.util.Collection;
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
            systemCode.setId(1);
            systemCode.setCodeName("insert into 2fxxk your name is '' where 1=1 ; sho");
            systemCode.setCodeFiter(15);
            systemCode.setUpdateTime(null);
            systemCode.setCreateTime(null);
//            plainSelect.setIntoTables(Arrays.asList(new Table(SystemCode.class.getSimpleName())));
            long t2 = System.currentTimeMillis();
            logger.info("============> time is : "+(t2-t1));

            Object strings = new String[]{"1", "2", "3"};
            Object ss2 = Arrays.asList(new String[]{"1", "2", "3"});
            logger.info("ss : " + strings.getClass().isArray());
            logger.info("ss : " + (ss2 instanceof Collection));

            String sql ="select * from SystemCode where 1=1 and id=1 and 1=3";
            Statement stmt = CCJSqlParserUtil.parse(sql);
            PlainSelect select = (PlainSelect) ((Select) stmt).getSelectBody();
            if(select.getWhere() instanceof EqualsTo){
                if(JsqlUtils.convertValueType(1) == ((EqualsTo)select.getWhere()).getLeftExpression() && JsqlUtils.convertValueType(1) == ((EqualsTo)select.getWhere()).getRightExpression() ){
                   select.setWhere(null);
                }
            }

            ConditionWrapper conditionWrapper = new ConditionWrapper(systemCode);
            conditionWrapper.ge("id")
            .in("id",JsqlUtils.convertValueTypeList(new Integer[]{1,2,3}))
                    .lt("codeFiter")
//                    .gt("id")
            ;


//            System.out.println(conditionWrapper.get());

//            sc = baseDaoNew.selectEntity(systemCode);
//            logger.info(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "SystemCode"));
//            logger.info(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "SystemCode"));
//            logger.info(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "SYSTEMCODE"));
//            logger.info(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "systemCode"));

//            baseDaoNew.updateEntity(systemCode);
//            baseDaoNew.updateEntityWithNamedQueryColumn(systemCode,"id,codeName");
//            String s = "UPDATE SystemCode SET codeName = 'fxxk your name is \\'\\' where 1=1 ; sho', updateTime = {ts '2019-03-10 09:24:44.773'} WHERE 1 = 1 AND id = 4 ";
//            baseDaoNew.update("BaseDao.updateEntity", new HashMap<String, String>() {{
//                put("baseSql", s.toString());
//            }});
//            baseDaoNew.deleteEntity(systemCode);
//            sc = baseDaoNew.insertEntityList(sc);
//            systemCode.setId(1);
            String ordreBy = "codeType asc,id desc";
//            Statement s = CCJSqlParserUtil.parse(ordreBy);
            List<SystemCode> result = baseDaoNew.selectEntity(systemCode,conditionWrapper,"id desc,updateTime asc");
            logger.info("show me "+ result.size());
            long t3 = System.currentTimeMillis();
            logger.info("============> time is : "+(t3-t2));
        } catch (Exception e) {
            logger.error("query error!", e);
        }
    }
}
