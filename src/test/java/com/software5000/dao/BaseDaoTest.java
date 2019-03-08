package com.software5000.dao;


import com.mysql.cj.log.LogFactory;
import com.software5000.base.BaseDao;
import com.software5000.base.BaseDaoNew;
import com.software5000.biz.entity.SystemCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-core.xml","classpath:spring-db.xml"})
//@Transactional
public class BaseDaoTest {

    private Logger logger = LoggerFactory.getLogger(BaseDaoTest.class);

    @Autowired
    BaseDao baseDao;

    @Autowired
    BaseDaoNew baseDaoNew;

    @Test
    public  void testSelectRecChannel(){

        try {
            List<SystemCode> sc = new ArrayList<>();

            for (int i = 0 ; i<3 ;i++) {
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

//            baseDaoNew.deleteEntity(systemCode);
//            sc = baseDaoNew.insertEntityList(sc);
//            systemCode.setId(1);
//            List<SystemCode> result = baseDao.selectEntity(systemCode);
//            logger.info("show me "+ result.size());
            logger.info("show me ");
        } catch (Exception e) {
            logger.error("query error!",e);
        }
    }
}
