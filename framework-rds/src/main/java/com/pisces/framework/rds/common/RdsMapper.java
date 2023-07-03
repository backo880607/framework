package com.pisces.framework.rds.common;

import com.pisces.framework.rds.annotation.RegisterMapper;
import com.pisces.framework.rds.provider.RdsProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Collection;
import java.util.List;

/**
 * rds映射器
 *
 * @author jason
 * @date 2022/12/07
 */
@RegisterMapper
public interface RdsMapper<T> {
    /**
     * 检查表
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    void checkTable();

    /**
     * 查询全部结果
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    List<T> selectAll();

    /**
     * 根据id进行查询
     *
     * @param id id
     * @return {@link T}
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    T selectById(Long id);

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ids id列表
     * @return {@link List}<{@link T}>
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    List<T> selectByIds(Collection<Long> ids);

    /**
     * 根据主键字段查询总数，方法参数必须包含完整的主键属性，查询条件使用等号
     *
     * @param id id
     * @return boolean
     */
    @SelectProvider(type = RdsProvider.class, method = "dynamicSQL")
    boolean existsById(Long id);

    /**
     * 保存一个实体，null的属性不会被保存, 会使用数据库默认值
     *
     * @param record 记录
     * @return int
     */
    @InsertProvider(type = RdsProvider.class, method = "dynamicSQL")
    int insert(T record);

    /**
     * 批量插入，支持批量插入的数据库可以使用，例如MySQL,H2等，另外该接口限制实体包含`id`属性并且必须为自增列
     *
     * @param recordList 记录列表
     * @return int
     */
    @InsertProvider(type = RdsProvider.class, method = "dynamicSQL")
    int insertBatch(Collection<T> recordList);

    /**
     * 批量插入 entity 数据，按 size 切分
     *
     * @param entities 插入的数据列表
     * @param size     切分大小
     * @return 影响行数
     */
    default int insertBatch(List<T> entities, int size) {
        if (size <= 0) {
            size = 1000;//默认1000
        }
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<T> list = entities.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += insertBatch(list);
        }
        return sum;
    }

    /**
     * 根据主键更新实体全部字段，null值不会被更新
     *
     * @param record 记录
     * @return int
     */
    @UpdateProvider(type = RdsProvider.class, method = "dynamicSQL")
    int update(T record);

    /**
     * 更新列表
     *
     * @param recordList 记录列表
     * @return int
     */
    @UpdateProvider(type = RdsProvider.class, method = "dynamicSQL")
    int updateBatch(Collection<T> recordList);

    /**
     * 批量更新 entity 数据，按 size 切分
     *
     * @param entities 插入的数据列表
     * @param size     切分大小
     * @return 影响行数
     */
    default int updateBatch(List<T> entities, int size) {
        if (size <= 0) {
            size = 1000;//默认1000
        }
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<T> list = entities.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += updateBatch(list);
        }
        return sum;
    }

    /**
     * 根据实体属性作为条件进行删除，查询条件使用等号
     *
     * @param id 记录
     * @return int
     */
    @DeleteProvider(type = RdsProvider.class, method = "dynamicSQL")
    int deleteById(Long id);

    /**
     * 按主键删除
     *
     * @param ids 键列表
     * @return int
     */
    @DeleteProvider(type = RdsProvider.class, method = "dynamicSQL")
    int deleteBatchByIds(Collection<Long> ids);

    /**
     * 根据多个 id 批量删除数据
     *
     * @param idList ids 列表
     * @param size   切分大小
     * @return 返回影响的行数
     */
    default int deleteBatchByIds(List<Long> idList, int size) {
        if (size <= 0) {
            size = 1000;//默认1000
        }
        int sum = 0;
        int entitiesSize = idList.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<Long> list = idList.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += deleteBatchByIds(list);
        }
        return sum;
    }

    /**
     * 批量删除
     *
     * @param recordList 记录列表
     * @return int
     */
    @DeleteProvider(type = RdsProvider.class, method = "dynamicSQL")
    int deleteBatch(Collection<T> recordList);

    /**
     * 批量删除 entity 数据，按 size 切分
     *
     * @param entities 插入的数据列表
     * @param size     切分大小
     * @return 影响行数
     */
    default int deleteBatch(List<T> entities, int size) {
        if (size <= 0) {
            size = 1000;//默认1000
        }
        int sum = 0;
        int entitiesSize = entities.size();
        int maxIndex = entitiesSize / size + (entitiesSize % size == 0 ? 0 : 1);
        for (int i = 0; i < maxIndex; i++) {
            List<T> list = entities.subList(i * size, Math.min(i * size + size, entitiesSize));
            sum += deleteBatch(list);
        }
        return sum;
    }
}
