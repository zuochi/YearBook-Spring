package com.school.base.rest;

import com.school.base.common.easyui.DataGridModel;
import com.school.base.common.easyui.DataGridRowModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * REST服务列表服务
 *
 * @author work
 */
@Controller
@RequestMapping("/rest")
public class RestController {

    @RequestMapping("")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("views/rest/index");
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping("/list")
    public DataGridModel list(HttpServletRequest request) throws ClassNotFoundException {
        String url = request.getRequestURL().toString();
        int index = url.indexOf("/", 10);
        String baseURL = url.substring(0, index);
        String contextPath = request.getContextPath();
        DataGridModel dataGridModel = new DataGridModel();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        //TODO:base-package
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.gz91pay");
        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class cls = Class.forName(beanDefinition.getBeanClassName());
            if (cls.equals(this.getClass())) {
                continue;
            }
            if (cls.isAnnotationPresent(RequestMapping.class)
                    && cls.isAnnotationPresent(Controller.class)) {
                RequestMapping clsMapping = (RequestMapping) cls.getAnnotation(RequestMapping.class);
                RestService restService = (RestService) cls.getAnnotation(RestService.class);
                Method[] methods = cls.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)
                            && method.isAnnotationPresent(ResponseBody.class)) {
                        RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                        String[] values = methodMapping.value();
                        for (String value : values) {
                            if (clsMapping.value().length > 0) {
                                if ((clsMapping.value()[0] + value).startsWith("/rest/")) {
                                    RestMethod restMethod = method.getAnnotation(RestMethod.class);
                                    
                                    //service
                                    DataGridRowModel dataGridRowModel = new DataGridRowModel();
                                    dataGridRowModel.addAttribute("service", restService != null
                                            && StringUtils.isNotBlank(restService.name())
                                            ? restService.name() : cls.getSimpleName());
                                    //method
                                    dataGridRowModel.addAttribute("method", restMethod != null
                                            && StringUtils.isNotBlank(restMethod.name())
                                            ? restMethod.name() : method.getName());
                                    //type
                                    StringBuilder type = new StringBuilder();
                                    RequestMethod[] requestMethods = methodMapping.method();
                                    for (RequestMethod requestMethod : requestMethods) {
                                        type.append(requestMethod.name()).append(" ");
                                    }
                                    if (requestMethods.length == 0) {
                                        type.append(RequestMethod.GET.name());
                                    }
                                    dataGridRowModel.addAttribute("type", type.toString());
                                    //url
                                    dataGridRowModel.addAttribute("url", baseURL + contextPath + clsMapping.value()[0] + value);
                                    //param
                                    List<String> params = new ArrayList();
                                    Class[] clses = method.getParameterTypes();
                                    Annotation[][] annotationses = method.getParameterAnnotations();
                                    for (int i = 0; i < clses.length; i++) {
                                        if (annotationses[i].length > 0 && annotationses[i][0].annotationType().equals(RestParam.class)) {
                                            params.add(clses[i].getName() + " " + ((RestParam) annotationses[i][0]).name());
                                        } else {
                                            params.add(clses[i].getName() + " arg" + i);
                                        }
                                    }
                                    dataGridRowModel.addAttribute("params", params);
                                    dataGridModel.addRow(dataGridRowModel);
                                }
                            }
                        }
                    }
                }
            }
        }
        dataGridModel.sort("service", "method", "type");
        return dataGridModel;
    }
}
