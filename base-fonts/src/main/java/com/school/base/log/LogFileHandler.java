package com.school.base.log;

import com.school.base.common.log.ILogHandler;
import com.school.base.domain.management.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 文件日志处理器，将日志保存至文件
 *
 * @author work
 */
@Service("logFileHandler")
public class LogFileHandler implements ILogHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(Log info, Exception e) {
        if (info != null) {
            this.logger.info(info.toString());
        }

        if (e != null) {
            this.logger.error(e.getMessage() != null ? e.getMessage() : "Unexpected exception occurs.", e);
        }
    }
}
