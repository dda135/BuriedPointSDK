package fanjh.mine.buriedpointapt;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import fanjh.mine.buriedpointannotation.Const;
import fanjh.mine.buriedpointannotation.IBuriedPointApt;
import fanjh.mine.buriedpointannotation.PageBackground;
import fanjh.mine.buriedpointannotation.PageShow;

/**
 * @author fanjh
 * @date 2018/2/9 10:23
 * @description
 * @note SupportedAnnotationTypes指定当前获取到的注解，相当于一个过滤器
 **/
@SupportedAnnotationTypes("fanjh.mine.buriedpointannotation.PageBackground")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AppBackgroundProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    /**
     * 用于初始化一些工具
     * 后续可以使用这些工具进行操作
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //用于打印日志
        messager = processingEnv.getMessager();
        //用于写出类文件
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //可以通过当前方法获取指定的注解
        Set<Element> set = (Set<Element>) roundEnv.getElementsAnnotatedWith(PageBackground.class);
        if(null == set){
            return false;
        }
        Map<String,String> caches = new HashMap<>();
        //遍历当前代码中所有的指定注解
        for (Element element : set) {
            //获取当前注解的作用对象
            if (element.getKind() == ElementKind.METHOD) {
                //这里实际上就是把有当前注解的类名和方法名进行缓存
                ExecutableElement executableElement = (ExecutableElement) element;
                TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();

                String className = typeElement.getQualifiedName().toString();
                log(className);

                if(executableElement.getParameters().size() > 0){
                    throw new IllegalArgumentException("当前注解标记方法不能有参数！");
                }

                TypeMirror typeMirror = executableElement.getReturnType();
                TypeKind typeKind = typeMirror.getKind();
                if(TypeKind.DECLARED != typeKind || !"java.util.Map<java.lang.String,java.lang.String>".equals(typeMirror.toString())){
                    throw new IllegalArgumentException("当前注解标记方法返回值类型有误！");
                }

                String methodName = executableElement.getSimpleName().toString();

                caches.put(className,methodName);
            }

        }
        //当前有指定的注解
        if(caches.size() > 0) {
            //通过javapoet生成指定的类

            FieldSpec fieldSpec = FieldSpec.builder(HashMap.class, "cache",
                    Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).
                    initializer("new HashMap<String,String>()").
                    build();

            CodeBlock.Builder staticBuilder = CodeBlock.builder();

            for(Map.Entry<String,String> entry:caches.entrySet()){
                staticBuilder.addStatement("cache.put($S,$S)", entry.getKey(), entry.getValue());
            }

            MethodSpec methodSpec = MethodSpec.methodBuilder("getMethod").
                    addModifiers(Modifier.PUBLIC).
                    addParameter(String.class,"className").
                    returns(String.class).
                    addStatement("return (String)cache.get(className)").
                    build();

            TypeSpec typeSpec = TypeSpec.classBuilder(Const.APP_BACKGROUND_CLASSNAME).
                    addModifiers(Modifier.PUBLIC).
                    addStaticBlock(staticBuilder.build()).
                    addSuperinterface(IBuriedPointApt.class).
                    addField(fieldSpec).
                    addMethod(methodSpec).
                    build();

            JavaFile javaFile = JavaFile.builder(Const.PACKAGE_NAME, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log("-----------------------------");
        return false;
    }

    /**
     * 打印日志
     * @param content
     */
    private void log(String content){
        messager.printMessage(Diagnostic.Kind.NOTE, content);
    }

}
