package com.school.base.common.web;

import java.lang.reflect.Method;
import java.util.Iterator;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 *
 * @author work
 */
@Service
public class UploadInterceptor implements MethodBeforeAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.isAnnotationPresent(Upload.class)) {
            //
            Class[] paramterTypes = method.getParameterTypes();
            if (args.length == 3 && args[0] instanceof MultipartHttpServletRequest
                    && paramterTypes[1].equals(String.class)
                    && paramterTypes[2].equals(byte[].class)) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) args[0];
                Iterator<String> names = multipartRequest.getFileNames();
                if (names.hasNext()) {
                    //load the inputstream
                    String name = names.next();
                    MultipartFile file = multipartRequest.getFile(name);

                    //fill the values
                    args[1] = file.getOriginalFilename();
                    args[2] = file.getBytes();
                }
            } else {
                String msg = target.getClass().getName() + "." + method.getName() 
                        + ": Method's parameter types don't match (HttpServletRequest,String,byte[])";
                this.logger.error(msg);
                throw new ServletException(msg);
            }
        }
    }
}
