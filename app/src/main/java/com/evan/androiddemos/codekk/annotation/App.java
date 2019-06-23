package com.evan.androiddemos.codekk.annotation;


/**
 * https://a.codekk.com/detail/Android/Trinea/%E5%85%AC%E5%85%B1%E6%8A%80%E6%9C%AF%E7%82%B9%E4%B9%8B%20Java%20%E6%B3%A8%E8%A7%A3%20Annotations
 */
public class App {

    @MethodInfo(author = "evan.ye", date = "2019/02/33", version = 2, arrays = {"abc","def"})
    public String getAppName() {
        return "AppName";
    }

    public static void main(String[] args) {

        App app = new App();
        Class cls = app.getClass();
//        for (Method method : cls.getMethods()) {
//            // RetentionPolicy.RUNTIME 也就是运行时 Annotation，解析： getAnnotation
//            Annotation[] annotations = method.getAnnotations();
//            for (Annotation annotation : annotations) {
//                System.out.println("annotation:" + annotation);
//            }
//
//            MethodInfo methodInfo = method.getAnnotation(
//                    MethodInfo.class);
//            if (methodInfo != null) {
//                System.out.println("method name:" + method.getName());
//                System.out.println("method author:" + methodInfo.author());
//                System.out.println("method version:" + methodInfo.version());
//                System.out.println("method date:" + methodInfo.date());
//                System.out.println("method arrays:" + methodInfo.arrays());
//            }
//        }

        // RetentionPolicy.CLASS 编译时期
        // ，解析
        // a. 自定义类集成自 AbstractProcessor
        // b. 重写其中的 process 函数
    }
}
