package org.camunda.consulting;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public class ZeebeExpressionResolver implements BeanFactoryAware {

    private BeanExpressionResolver beanExpressionResolver;
    private BeanFactory beanFactory;
    private BeanExpressionContext expressionContext;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if(beanFactory instanceof ConfigurableBeanFactory) {
            this.beanExpressionResolver = ((ConfigurableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableBeanFactory) beanFactory, null);
        }
    }

    public <T> T resolve(String value){
        return (T) this.beanExpressionResolver.evaluate(value, this.expressionContext);
    }
}
