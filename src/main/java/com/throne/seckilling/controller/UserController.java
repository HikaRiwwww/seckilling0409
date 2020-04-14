package com.throne.seckilling.controller;

import com.throne.seckilling.controller.view_model.UserVO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.error.EnumBusinessError;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.UserService;
import com.throne.seckilling.service.model.UserModel;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;

/**
 * 处理用户相关的Controller
 * com.throne.seckilling.controller
 * Created by throne on 2020/4/9
 */


@Controller("user")
@RequestMapping(value = "/user")
@CrossOrigin
public class UserController extends BaseController {

    @Resource(name = "userService")
    private UserService userService;

    @Autowired
    // 由spring帮助注入request对象，该对象是单例，但是实现了ThreadLocal
    private HttpServletRequest request;

    /**
     * 根据id调用service返回对应用户信息给前端
     *
     * @param userId 用户id
     */
    @RequestMapping("/get_user")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer userId) throws BusinessException {
        UserModel userModel = userService.getUserById(userId);
        UserVO userVO = new UserVO();

        if (userModel == null) {
            throw new BusinessException(EnumBusinessError.USER_NOT_EXSITS);
        }
        BeanUtils.copyProperties(userModel, userVO);
        return CommonReturnType.create(userVO);
    }

    /**
     * 获取手机号，发送验证码
     *
     * @param telephone 手机号
     * @return 短信发送成功的json信息 或在手机号不符合规则时抛出异常
     */
    @RequestMapping(value = "/get_otp",
            method = {RequestMethod.POST},
            consumes = {CONTENT_TYPE_FORMED}
    )
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) throws BusinessException {
        // 验证手机号的格式
        if (!phoneNumIsLegal(telephone)) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "手机号格式不正确！");
        }
        // 生成验证码
        Random random = new Random();
        int otpCode = random.nextInt(89999) + 10000;
        // 将手机号与对应验证码存入session
        HttpSession session = request.getSession();
        session.setAttribute("telephone", telephone);
        session.setAttribute("otpCode", Integer.toString(otpCode));

        // 调用发送短信接口 略过

        System.out.println("手机号： " + telephone + " 验证码： " + otpCode);
        return CommonReturnType.create("success", "短信发送成功");

    }

    /**
     * 用户注册Controller
     *
     * @param name    用户名
     * @param gender  性别
     * @param age     年龄
     * @param otpCode 手机验证码
     * @return 注册成功与否的响应信息
     */
    @RequestMapping(value = "/register",
            method = {RequestMethod.POST},
            consumes = {CONTENT_TYPE_FORMED}
    )
    @ResponseBody
    public CommonReturnType userRegister(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "gender") Byte gender,
            @RequestParam(name = "age") Integer age,
            @RequestParam(name = "otpCode") String otpCode,
            @RequestParam(name = "password") String password
    ) throws BusinessException {
        // 验证手机号与验证码是否匹配
        HttpSession session = this.request.getSession();
        String telephone = (String) session.getAttribute("telephone");
        String otpCodeInSession = (String) session.getAttribute("otpCode");

        if (telephone == null) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "请先输入手机号并获取验证码");
        }
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, otpCodeInSession)) {
            throw new BusinessException(EnumBusinessError.PARAMETER_VALIDATION_ERROR, "验证码错误");
        }
        String salt = "#*@!9($Hjksyf98w";

        // 构建UserModel
        UserModel userModel = new UserModel();
        // 二进制数据长度不为16会返回null
        String encryptPassword = null;
        try {
            encryptPassword = encryptPasswordBySha256(password+salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BusinessException(EnumBusinessError.UNKNOWN_ERROR);
        }
        userModel.setEncryptPwd(encryptPassword);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setName(name);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("by phone");
        userModel.setThirdPartyId("");

        System.out.println(userModel);
        // 调用service层处理用户注册
        userService.register(userModel);
        return CommonReturnType.create("success", "注册成功");
    }

    @RequestMapping("/insert_telephone")
    public String insertTelephone() {
        return "/html/get_otp_code.html";
    }


    /**
     * 校验手机号格式是否合法 11位 纯数字
     *
     * @return 合法为true，不合法为false
     */
    public boolean phoneNumIsLegal(String phoneNumber) {
        String numbers = "1234567890";
        if (phoneNumber.length() == 11) {
            char[] chars = phoneNumber.toCharArray();
            boolean flag = true;
            for (char aChar : chars) {
                if (numbers.indexOf(aChar) == -1) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
    }

    /**
     * 对加盐的密码用sha256算法加密
     * @param saltedPassword  加盐的密码
     * @return  加密后的密码
     * @throws NoSuchAlgorithmException
     */
    public String encryptPasswordBySha256(String saltedPassword) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = messageDigest.digest(saltedPassword.getBytes());
        return new String(Hex.encodeHex(bytes));


    }

}
