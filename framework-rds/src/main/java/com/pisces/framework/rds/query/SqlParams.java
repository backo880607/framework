package com.pisces.framework.rds.query;

import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.Brackets;
import com.pisces.framework.core.query.condition.OperatorQueryCondition;
import com.pisces.framework.core.query.condition.OperatorSelectCondition;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.utils.lang.ClassUtils;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.rds.config.RdsConstant;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * sql参数
 *
 * @author jason
 * @date 2023/07/12
 */
public class SqlParams {

    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    public static Object[] getValueArray(QueryWrapper qw) {
        List<Object> withValues = null;
        List<Object> columnValues = null;
//        List<QueryColumn> selectColumns = qw.getSelectColumns();
//        if (CollectionUtils.isNotEmpty(selectColumns)) {
//            for (QueryColumn selectColumn : selectColumns) {
//                if (selectColumn instanceof HasParamsColumn) {
//                    Object[] paramValues = ((HasParamsColumn) selectColumn).getParamValues();
//                    if (CollectionUtils.isNotEmpty(paramValues)) {
//                        if (columnValues == null) {
//                            columnValues = new ArrayList<>(paramValues.length);
//                        }
//                        columnValues.addAll(Arrays.asList(paramValues));
//                    }
//                }
//            }
//        }

        //select 子查询的参数：select * from (select ....)
        List<Object> tableValues = null;
        List<QueryTable> queryTables = qw.getQueryTables();
        if (CollectionUtils.isNotEmpty(queryTables)) {
            for (QueryTable queryTable : queryTables) {
                Object[] tableValueArray = queryTable.getValueArray();
                if (tableValueArray.length > 0) {
                    if (tableValues == null) {
                        tableValues = new ArrayList<>(tableValueArray.length);
                    }
                    tableValues.addAll(Arrays.asList(tableValueArray));
                }
            }
        }

        //join 子查询的参数：left join (select ...)
        List<Object> joinValues = null;
//        List<Join> joins = qw.getJoins();
//        if (CollectionUtil.isNotEmpty(joins)) {
//            for (Join join : joins) {
//                QueryTable joinTable = join.getQueryTable();
//                Object[] valueArray = joinTable.getValueArray();
//                if (valueArray.length > 0) {
//                    if (joinValues == null) {
//                        joinValues = new ArrayList<>(valueArray.length);
//                    }
//                    joinValues.addAll(Arrays.asList(valueArray));
//                }
//                QueryCondition onCondition = join.getOnCondition();
//                Object[] values = WrapperUtil.getValues(onCondition);
//                if (values.length > 0) {
//                    if (joinValues == null) {
//                        joinValues = new ArrayList<>(values.length);
//                    }
//                    joinValues.addAll(Arrays.asList(values));
//                }
//            }
//        }

        //where 参数
        Object[] whereValues = getValues(qw.getWhereQueryCondition());

        //having 参数
        Object[] havingValues = getValues(qw.getHavingQueryCondition());

        Object[] paramValues = CollectionUtils.concat(whereValues, havingValues);

        //unions 参数
//        if (CollectionUtils.isNotEmpty(unions)) {
//            for (UnionWrapper union : unions) {
//                QueryWrapper queryWrapper = union.getQueryWrapper();
//                paramValues = CollectionUtils.concat(paramValues, queryWrapper.getValueArray());
//            }
//        }

        Object[] returnValues = withValues == null ? RdsConstant.EMPTY_ARRAY : withValues.toArray();
        returnValues = columnValues != null ? CollectionUtils.concat(returnValues, columnValues.toArray()) : returnValues;
        returnValues = tableValues != null ? CollectionUtils.concat(returnValues, tableValues.toArray()) : returnValues;
        returnValues = joinValues != null ? CollectionUtils.concat(returnValues, joinValues.toArray()) : returnValues;
        returnValues = CollectionUtils.concat(returnValues, paramValues);

        return returnValues;
    }

    static Object[] getValues(QueryCondition condition) {
        if (condition == null) {
            return RdsConstant.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();
        getValues(condition, params);

        return params.isEmpty() ? RdsConstant.EMPTY_ARRAY : params.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> params) {
        if (condition == null) {
            return;
        }

        Object value = null;
        if (condition instanceof Brackets brackets) {
            if (brackets.checkEffective()) {
                value = getValues(brackets.getChildCondition());
            }
        } else if (condition instanceof OperatorQueryCondition operator) {
            value = getValues(operator.getChild());
        } else if (condition instanceof OperatorSelectCondition operator) {
            value = getValueArray(operator.getQueryWrapper());
        } else {
            value = condition.getValue();
        }

        if (value == null
                || value instanceof QueryColumn) {
            getValues(condition.getNext(), params);
            return;
        }

        addParam(params, value);
        getValues(condition.getNext(), params);
    }

    private static void addParam(List<Object> paras, Object value) {
        if (value == null) {
            paras.add(null);
        } else if (ClassUtils.isArray(value.getClass())) {
            for (int i = 0; i < Array.getLength(value); i++) {
                addParam(paras, Array.get(value, i));
            }
        } else if (value instanceof QueryWrapper) {
            Object[] valueArray = ((QueryWrapper) value).getValueArray();
            paras.addAll(Arrays.asList(valueArray));
        } else if (value.getClass().isEnum()) {
//            EnumWrapper enumWrapper = EnumWrapper.of(value.getClass());
//            if (enumWrapper.hasEnumValueAnnotation()) {
//                paras.add(enumWrapper.getEnumValue((Enum) value));
//            } else {
//                paras.add(value);
//            }
            paras.add(value);
        } else {
            paras.add(value);
        }

    }
}
