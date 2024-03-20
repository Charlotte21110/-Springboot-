package com.design.filmr.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    //用来统一前后端的数据

    //重载，方便处理更懂的数据，成功，或者失败返回的状态码
    public static <T>Result<T> success(){
        return new Result<>(20000,"success",null);
    }
    public static <T>Result<T> success(T data){
        return new Result<>(20000,"success",data);
    }
    public static <T>Result<T> success(T data,String message){
        return new Result<>(20000,message,data);
    }
    public static <T>Result<T> success(String message){
        return new Result<>(20000,message,null);
    }


    public static <T>Result<T> fail(){
        return new Result<>(20000,"fail",null);
    }
    public static <T>Result<T> fail(Integer code){
        return new Result<>(20000,"fail",null);
    }

    public static <T>Result<T> fail(Integer code,String message){
        return new Result<>(code,message,null);
    }
    public static <T>Result<T> fail(Integer code,String message,T data){
        return new Result<>(code,message,data);
    }
    public static <T>Result<T> fail(String message){
        return new Result<>(20001,message,null);
    }
}
