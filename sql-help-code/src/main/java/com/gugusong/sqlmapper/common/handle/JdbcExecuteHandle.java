package com.gugusong.sqlmapper.common.handle;

import com.gugusong.sqlmapper.Example;
import com.gugusong.sqlmapper.common.beans.BeanWrapper;

/**
 * 数据库操作统一处理
 *
 * @author you
 */
public interface JdbcExecuteHandle {
    void selectHandle(Example example, BeanWrapper entityWrapper);
    void updateHandle(Example example, BeanWrapper entityWrapper, Object entity);
    void deleteHandle(Example example, BeanWrapper entityWrapper);
    void insertHandle(BeanWrapper entityWrapper, Object entity);
}
