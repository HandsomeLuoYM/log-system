package com.system.aop;

import com.system.common.ResultStatusCode;
import com.system.pojo.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * @author Ming
 * @date 2020/4/26 - 17:16
 * @describe 全局异常处理器，处理控制层异常对象
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 全局异常处理
     * @param e 异常对象（全局）
     * @return 返回json对象
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult globalExceptionHandle(Exception e) {
//        e.printStackTrace();
        logger.error("[系统异常：]{}", e);
        return CommonResult.failed(ResultStatusCode.FAILED,e.getMessage());
    }

//    /**
//     * 非法IP地址异常
//     * @param e 异常对象（全局）
//     * @return 返回json对象
//     */
//    @ExceptionHandler(value = IllegalIPException.class)
//    public CommonResult illegalIPExceptionHandle(Exception e) {
////        e.printStackTrace();
//        logger.error("[系统异常：]{}", e.getMessage());
//        return CommonResult.failed(ResultStatusCode.FAILED, e.getMessage());
//    }

    /**
     * 照片太大异常
     * @param e 异常
     * @return 封装并返回
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public CommonResult fileSizeLimitExceededExceptionHandler(Exception e) {
        return CommonResult.failed(ResultStatusCode.FAILED, "照片尺寸需要小于1M！");
    }

//    /**
//     * TokenInvalidException
//     * @param e token 过期异常
//     * @return 返回json对象
//     */
//    @ExceptionHandler(value = TokenInvalidException.class)
//    public CommonResult tokenInvalidHandler(TokenInvalidException e) {
//        return CommonResult.failed(ResultStatusCode.UNAUTHORIZED, e.getMessage());
//    }

    /**
     * 用户丢失错误
     * @param e 异常对象
     * @return 返回json对象
     */
    @ExceptionHandler(value = {MissingServletRequestPartException.class, MissingServletRequestParameterException.class})
    public CommonResult userMissingHandler(Exception e) {
        return CommonResult.failed(ResultStatusCode.NOT_FOUND, e.getMessage());
    }

    /**
     * 处理所有接口数据验证异常
     * @param e 异常对象
     * @return 返回json数据
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        return CommonResult.failed(ResultStatusCode.BAD_REQUEST,e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 校验错误
     * @param e 异常对象
     * @return 返回json数据
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult invalidFormatExceptionHandler(HttpMessageNotReadableException e){
        return CommonResult.failed(ResultStatusCode.BAD_REQUEST);
    }

    /**
     * GET和POST的请求方法错误
     * @param e 异常
     * @return 直接Json返回
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CommonResult httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e){
        return CommonResult.failed(ResultStatusCode.URL_TO_LONG);
    }

    /**
     * 参数匹配错误
     * @param e 异常
     * @return 直接Json返回
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e){
        return CommonResult.failed(ResultStatusCode.BAD_REQUEST);
    }
}