package io.github.samwright.framework.plugin;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * User: Sam Wright Date: 17/08/2013 Time: 14:02
 */
public class Activator implements BundleActivator {
    public static void activateElement(ElementController controller) {

    }

    public static void activateWorkflow(WorkflowController controller) {

    }

    public static void activateWorkflowContainer(WorkflowContainerController controller) {

    }

    private static void activateProcessor(ModelController controller) {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        toolbox.getChildren().add(controller);
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
    }
}
