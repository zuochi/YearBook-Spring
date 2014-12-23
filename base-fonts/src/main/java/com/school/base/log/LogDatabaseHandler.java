package com.school.base.log;

import com.school.base.common.log.ILogHandler;
import com.school.base.domain.management.Log;
import com.school.base.service.face.management.ILogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据库日志处理器，将日志保存至数据库
 *
 * @author work
 */
@Service("logDatabaseHandler")
public class LogDatabaseHandler implements ILogHandler {

    @Autowired
    private ILogService logService;

    @Override
    public void handle(Log info, Exception e) {
        if (info != null) {
            this.logService.insert(info);
        }
    }
}
