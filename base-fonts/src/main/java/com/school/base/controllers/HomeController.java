package com.school.base.controllers;

import com.school.base.areas.fonts.controllers.AuditController;
import com.school.base.areas.fonts.controllers.LogController;
import com.school.base.common.easyui.TreeModel;
import com.school.base.common.easyui.TreeNodeModel;
import com.school.base.common.security.IContextService;

import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    private IContextService contextService;

    @RequestMapping(value = {"/home/index", "/home", ""}, method = RequestMethod.GET)
    public String index(Locale locale, Model model) {
        return "views/home/index";
    }

    /**
     * 自动根据控制器生成默认菜单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/home/menu"}, method = RequestMethod.GET)
    public TreeModel getMenu() {
        String username = this.contextService.getUsername();
        return this.getMemu(username);
    }

    @SuppressWarnings("unchecked")
    private TreeModel getMemu(String username) {
        TreeModel model = new TreeModel();
        Integer id = 0;

        TreeNodeModel managementNode = new TreeNodeModel();
        managementNode.setId(id++);
        managementNode.setText("系统管理");
        managementNode.close();
        model.add(managementNode);

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.gz91pay");
        for (BeanDefinition beanDefinition : beanDefinitions) {
            try {
                Class cls = Class.forName(beanDefinition.getBeanClassName());
                if (cls.isAnnotationPresent(RequestMapping.class) && cls.isAnnotationPresent(Controller.class)) {
                    RequestMapping requestMapping = (RequestMapping) cls.getAnnotation(RequestMapping.class);
                    if (requestMapping == null || requestMapping.value().length == 0) {
                        continue;
                    }

                    Controller controller = (Controller) cls.getAnnotation(Controller.class);
                    if (controller.value() == null || controller.value().length() == 0) {
                        continue;
                    }

                    if (cls.equals(LogController.class)) {
                        //
                        TreeNodeModel userLogNode = new TreeNodeModel();
                        userLogNode.setId(id++);
                        userLogNode.setText("用户日志");
                        userLogNode.getAttributes().put("url", requestMapping.value()[0] + "?category=1");
                        managementNode.getChildren().add(userLogNode);
                        //
                        TreeNodeModel systemLogNode = new TreeNodeModel();
                        systemLogNode.setId(id++);
                        systemLogNode.setText("系统日志");
                        systemLogNode.getAttributes().put("url", requestMapping.value()[0] + "?category=2");
                        managementNode.getChildren().add(systemLogNode);
                        continue;
                    } else if (cls.equals(AuditController.class)) {
                        //
                        TreeNodeModel auditNode = new TreeNodeModel();
                        auditNode.setId(id++);
                        auditNode.setText("审计信息");
                        auditNode.getAttributes().put("url", requestMapping.value()[0]);
                        managementNode.getChildren().add(auditNode);
                        continue;
                    }

                    try {
                        cls.getMethod("index");
                    } catch (NoSuchMethodException e) {
                        continue;
                    }

                    TreeNodeModel nodeModel = new TreeNodeModel();
                    nodeModel.setId(id++);
                    nodeModel.setText(controller.value());
                    nodeModel.getAttributes().put("url", requestMapping.value()[0]);
                    model.add(nodeModel);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        return model;
    }
}
