package com.pisces.framework.core.utils.io;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * 复制器抽象类<br>
 * 抽象复制器抽象了一个对象复制到另一个对象，通过实现copy()方法实现复制逻辑。<br>
 *
 * @param <T> 拷贝的对象
 * @param <C> 本类的类型。用于set方法返回本对象，方便流式编程
 * @author Looly
 * @since 3.0.9
 */
public abstract class SrcToDestCopier<T, C extends SrcToDestCopier<T, C>> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 源
     */
    protected T src;
    /**
     * 目标
     */
    protected T dest;
    /**
     * 拷贝过滤器，可以过滤掉不需要拷贝的源
     */
    protected Predicate<T> copyFilter;

    //-------------------------------------------------------------------------------------------------------- Getters and Setters start

    /**
     * 获取源
     *
     * @return 源
     */
    public T getSrc() {
        return src;
    }

    /**
     * 设置源
     *
     * @param src 源
     * @return this
     */
    public C setSrc(T src) {
        this.src = src;
        return (C) this;
    }

    /**
     * 获得目标
     *
     * @return 目标
     */
    public T getDest() {
        return dest;
    }

    /**
     * 设置目标
     *
     * @param dest 目标
     * @return this
     */
    public C setDest(T dest) {
        this.dest = dest;
        return (C) this;
    }

    /**
     * 获得过滤器
     *
     * @return 过滤器
     */
    public Predicate<T> getCopyFilter() {
        return copyFilter;
    }

    /**
     * 设置过滤器
     *
     * @param copyFilter 过滤器
     * @return this
     */
    public C setCopyFilter(Predicate<T> copyFilter) {
        this.copyFilter = copyFilter;
        return (C) this;
    }

    /**
     * 复制
     *
     * @return {@link T}
     */
    abstract T copy();
}
