package com.throne.seckilling.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 在服务器启动时构造一个validator，用于数据验证
 */
@Component(value = "validator")
public class ValidatorImpl implements InitializingBean {
    private Validator validator;
    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();

    }
    public ValidationResult validateBean(Object bean){
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> validate = validator.validate(bean);
        if (validate.size() > 0){
            // 校验有错误
            result.setError(true);
            validate.forEach(constraintViolation -> {
                String propertyName = constraintViolation.getPropertyPath().toString();
                String errMsg = constraintViolation.getMessage();
                result.getResultMap().put(propertyName, errMsg);
            });
        }return result;
    }
}
