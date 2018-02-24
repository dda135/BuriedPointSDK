package fanjh.mine.buriedpointannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* @author fanjh
* @date 2018/2/9 13:50
* @description 用于标记当前页面展示的时候上报的数据
* @note
**/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface PageShow {
    boolean useDefaultValue() default false;

}
