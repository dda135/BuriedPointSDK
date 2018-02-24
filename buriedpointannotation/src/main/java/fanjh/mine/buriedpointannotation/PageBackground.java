package fanjh.mine.buriedpointannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @author fanjh
* @date 2018/2/9 13:50
* @description 用于标记当前应用进入后台的时候当前页面应该上报的数据
* @note
**/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PageBackground {
}
