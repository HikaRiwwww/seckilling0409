package com.throne.seckilling.service.impl;

import com.throne.seckilling.dao.UserDOMapper;
import com.throne.seckilling.dao.UserPasswordDOMapper;
import com.throne.seckilling.data_object.UserDO;
import com.throne.seckilling.data_object.UserPasswordDO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.service.UserService;
import com.throne.seckilling.service.model.UserModel;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用于处理用户信息的service
 * 用户模型在数据库层面涉及UserDOMapper的所有信息和UserPasswordDOMapper的encryptPwd，
 * 在此合并为完整的UserModel
 * com.throne.seckilling.service.impl
 * Created by throne on 2020/4/9
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    /**
     * @param id 用户id
     * @return 完整的用户信息模型
     */
    @Override
    public UserModel getUserById(Integer id) {
        UserModel userModel = new UserModel();
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return null;
        }
        BeanUtils.copyProperties(userDO, userModel);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);
        if (userPasswordDO != null) {
            userModel.setEncryptPwd(userPasswordDO.getEncryptPwd());
        }
        return userModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserModel userModel) throws BusinessException {
        // 安全检查
        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "不合法的用户");
        }
        if (StringUtils.isEmpty(userModel.getName()) ||
                userModel.getGender() == null ||
                userModel.getAge() == null ||
                StringUtils.isEmpty(userModel.getTelephone())) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "用户参数有误");
        }
        // model转换并写入数据库
        UserDO userDO = convertModelToUserDO(userModel);
        userDOMapper.insertSelective(userDO);
        UserPasswordDO userPasswordDO = convertModelToPasswordDO(userModel, userDO);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    @Override
    public UserModel loginValidate(String telephone, String encryptPassword) throws BusinessException {

        UserDO userDO = userDOMapper.selectByTelephone(telephone);
        UserModel userModel = convertUserDOToUserModel(userDO);
        if(userModel==null){
            throw new BusinessException(EnumBusinessError.USER_LOGIN_FAILED);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userModel.getId());
        if((userPasswordDO==null) || !encryptPassword.equals(userPasswordDO.getEncryptPwd())){
            throw new BusinessException(EnumBusinessError.USER_LOGIN_FAILED);
        }

        userModel = convertUserPasswordDOToUserModel(userPasswordDO, userModel);
        return userModel;
    }

    public UserDO convertModelToUserDO(UserModel userModel) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    public UserPasswordDO convertModelToPasswordDO(UserModel userModel, UserDO userDO) {
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncryptPwd(userModel.getEncryptPwd());
        userPasswordDO.setUserId(userDO.getId());
        return userPasswordDO;
    }

    public UserModel convertUserDOToUserModel(UserDO userDO){
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel);
        return userModel;
    }

    public UserModel convertUserPasswordDOToUserModel(UserPasswordDO userPasswordDO, UserModel userModel){
        userModel.setEncryptPwd(userPasswordDO.getEncryptPwd());
        return userModel;
    }
}
