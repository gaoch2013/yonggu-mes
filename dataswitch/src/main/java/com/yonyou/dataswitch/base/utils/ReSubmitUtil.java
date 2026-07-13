package com.yonyou.dataswitch.base.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyou.dataswitch.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 幂等工具类
 * @author: yonyou
 **/
@Slf4j
public class ReSubmitUtil {

    //MDD幂等性传参
    private final static String RESUbMIT_CHECK_KEY = "resubmitCheckKey";
    //配置可能存在的基础路径
    private final static String BASE_DATA = "data";
    //接口请求类型强制校验参数
    private final static String STATUS_INSERT = "insert";
    //接口状态参数
    private final static String STATUS = "_status";
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求JSON中自动加入幂等key, MD5 32位
     * @param t 请求数据
     * @param <T> 任意数据类型
     * @return 加入幂等key后的请求JSON
     */
    public static <T>T resubmitCheckKey(T t){
        try {
            String resubmitCheckKeyV = md5Encode(t);
            Object reqData = getRealData(t);
            //checkInfType(reqData); //校验是否为保存方法
            BeanUtils.setProperty(reqData,RESUbMIT_CHECK_KEY, resubmitCheckKeyV);
        }catch (Exception e){
            throw new BusinessException("幂等参数添加异常");
        }
        return t;
    }

    /**
     *  请求JSON中自动加入幂等key, MD5 16位 +时间维度
     * @param t 请求数据
     * @param dateFormat   日期格式  YYYYMMdd  YYYYMM
     * @param <T> 任意数据类型
     * @return 加入幂等key后的请求JSON
     */
    public static <T>T resubmitCheckKey(T t,String dateFormat){
        try {
            String resubmitCheckKeyV = md5EncodeTime(t,dateFormat);
            Object reqData = getRealData(t);
            BeanUtils.setProperty(reqData,RESUbMIT_CHECK_KEY, resubmitCheckKeyV);
        }catch (Exception e){
            throw new BusinessException("幂等参数添加异常");
        }
        return t;
    }


    private static Object getRealData(Object object) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String isData = BeanUtils.getProperty(object,BASE_DATA);
        if(!StringUtils.isEmpty(isData)){
            object = PropertyUtils.getProperty(object,BASE_DATA);
        }
        return object;
    }

    /**
     * md5 32位
     * @param object 请求JSON
     * @return string
     */
    public static String md5Encode(Object object) throws JsonProcessingException {
        byte[] bytes =  objectMapper.writeValueAsBytes(object);
        return DigestUtils.md5DigestAsHex(bytes);
    }

    /**
     *  md5 16位+ 时间
     * @param object  请求JSON
     * @param dateFormat  日期格式  YYYYMMdd  YYYYMM
     * @return string
     */
    public static String md5EncodeTime(Object object,String dateFormat) throws JsonProcessingException {
        byte[] bytes =  objectMapper.writeValueAsBytes(object);
        SimpleDateFormat date = new SimpleDateFormat(dateFormat);
        return DigestUtils.md5DigestAsHex(bytes).substring(8, 24) + date.format(new Date());
    }

    /**
     * 暂不做校验
     * @param object
     * @throws Exception
     */
//    private static void checkInfType(Object object) throws Exception {
//        String isHasData = BeanUtils.getProperty(object,STATUS);
//        if(StringUtils.isEmpty(isHasData)||!STATUS_INSERT.equals(StringUtils.trim(isHasData).toLowerCase())){
//            throw new Exception("请确定是保存接口，且放在data值里");
//        }
//    }

}
