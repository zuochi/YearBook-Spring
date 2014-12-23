package com.school.base.common.log;

import com.school.base.domain.management.Log;

/**
 * 日志处理器接口
 *
 * @author work
 */
public interface ILogHandler {

    void handle(Log info, Exception e);
}
