package com.pisces.framework.rds.handler;

import com.pisces.framework.type.MultiEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 多枚举类型处理程序
 *
 * @author jason
 * @date 2022/12/07
 */
public class MultiEnumTypeHandler extends BaseTypeHandler<MultiEnum<?>> {
    private final Class<? extends MultiEnum<?>> classType;

    public MultiEnumTypeHandler(Class<? extends MultiEnum<?>> classType) {
        this.classType = classType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MultiEnum<?> parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public MultiEnum<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }

        MultiEnum<?> result = BeanUtils.instantiateClass(classType);
        result.setValue(value);
        return result;
    }

    @Override
    public MultiEnum<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }

        MultiEnum<?> result = BeanUtils.instantiateClass(classType);
        result.setValue(value);
        return result;
    }

    @Override
    public MultiEnum<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }

        MultiEnum<?> result = BeanUtils.instantiateClass(classType);
        result.setValue(value);
        return result;
    }

}
