package com.software5000.dao;


import com.mysql.cj.log.LogFactory;
import com.software5000.base.BaseDao;
import com.software5000.biz.entity.SystemCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-core.xml","classpath:spring-db.xml"})
//@Transactional
public class BaseDaoTest {

    private Logger logger = LoggerFactory.getLogger(BaseDaoTest.class);

    @Autowired
    BaseDao baseDao;

    @Test
    public  void testSelectRecChannel(){

        try {
            SystemCode systemCode = new SystemCode();
            systemCode.setId(1);
            List<SystemCode> result = baseDao.selectEntity(systemCode);
            logger.info("show me "+ result.size());
        } catch (Exception e) {
            logger.error("query error!",e);
        }
    }
}
