package com.wuwei.wuwei.demos.aspect;
@Deprecated
public class TokneCheck {


//    @Around("execution(public * com.wuwei.wuwei.demos.controller.UserController.*(..))")
//    public Object  tokenCheck(ProceedingJoinPoint joinPoint) throws Throwable {
//        final Signature signature = joinPoint.getSignature();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        final String[] names = methodSignature.getParameterNames();
//        final Object[] args = joinPoint.getArgs();
//
//        for (int i = 0; i < names.length; i++) {
//            if("token".equals(names[i])){
//                try {
//                    String token = (String) args[i];
//                    System.out.println("有token的用户发起了校验:" + token);
//                    String openid = JWTutil.getClaimByToken(token).get("openid", String.class);
//                    String userToken = userMapper.getUserToken(openid);
//                    if (userToken.equals(token)) {
//                        return joinPoint.proceed();
//                    } else {
//                       return "aop fail";
//                    }
//                } catch (Exception e) {
//                    return "aop fail";
//                }
//
//            }
//        }
//        System.out.println(map);
//        return;
//    }


}
