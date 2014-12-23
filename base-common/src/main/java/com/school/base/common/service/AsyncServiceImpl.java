package com.school.base.common.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author work
 */
@Service
public class AsyncServiceImpl implements AsyncService {

    @Async
    @Override
    public void call(Runnable runnable) {
        runnable.run();
    }
}
