package com.throne.seckilling.service;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.service.model.UserModel;

/**
 * com.throne.seckilling.service
 * Created by throne on 2020/4/9
 */
public interface UserService {
    public UserModel getUserById(Integer id);

    public void register(UserModel userModel) throws BusinessException;

    UserModel loginValidate(String telephone, String encryptPassword) throws BusinessException;
}
