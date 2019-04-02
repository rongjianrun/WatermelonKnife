package com.rjr.watermelonknife;

/**
 * 所有生成的java文件都实现了这个接口
 * @param <T>
 */
public interface ViewBinder<T> {

    void bind(T t);
}
