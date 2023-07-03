package com.pisces.framework.rds.query;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SqlExecutor {

    /**
     * 获取SqlSessionFactory
     */
    private static SqlSessionFactory getSqlSessionFactory() {
        return AppUtils.getBean(SqlSessionFactory.class);
    }

    /**
     * 获取Session 默认自动提交
     */
    private static SqlSession sqlSession() {
        return SqlSessionUtils.getSqlSession(getSqlSessionFactory());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    private static void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
    }

    public static <T extends BeanObject> T fetchOne(QueryWrapper qw, Class<T> beanClass) {
        final String sql = SqlTools.toSql(qw);
        final Object[] args = SqlParams.getValueArray(qw);
        SqlSession sqlSession = sqlSession();
        try (Connection connection = sqlSession.getConnection()) {
            SqlRunner sqlRunner = new SqlRunner(connection);
            Map<String, Object> data = sqlRunner.selectOne(sql, args);
            if (data == null || data.isEmpty()) {
                return null;
            }
            return ObjectUtils.convertBean(data, beanClass);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeSqlSession(sqlSession);
        }
        return null;
    }

    public static <T extends BeanObject> List<T> fetch(QueryWrapper qw, Class<T> beanClass) {
        final String sql = SqlTools.toSql(qw);
        final Object[] args = SqlParams.getValueArray(qw);
        SqlSession sqlSession = sqlSession();
        try (Connection connection = sqlSession.getConnection()) {
            SqlRunner sqlRunner = new SqlRunner(connection);
            List<Map<String, Object>> data = sqlRunner.selectAll(sql, args);
            if (data == null || data.isEmpty()) {
                return null;
            }
            return ObjectUtils.convertBeanList(data, beanClass);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeSqlSession(sqlSession);
        }
        return null;
    }
}
