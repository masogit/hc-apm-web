package com.ge.apm.domain.pure.compute;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.*;
import java.util.Stack;

public class Functions {


    public static final Map<String,String> FUNCTION_MAP = ImmutableMap.<String,String>builder()
            .put("sum","sumFunction")
            .put("avg","avgFunction")
            .put("max","maxFunction")
            .put("min","minFunction")
            .put("if","ifFunction")
            .put("abs","absFunction")
            .put("eq","equalsFunction")
            .put("not","notFunction")
            .put("and","andFunction")
            .put("or","orFunction")
            .put("stdev","stdevFunction")
            .build();

    /**
     * 求和
     *
     * @param stack
     * @return
     */
    public static BigDecimal sumFunction(Stack stack) {
        BigDecimal sum = new BigDecimal("0");
        while (!stack.isEmpty()) {
            sum = sum.add(new BigDecimal(stack.pop().toString()));
        }
        return sum;
    }

    /**
     * 求平均值
     *
     * @param stack
     * @return
     */
    public static BigDecimal avgFunction(Stack stack) {
        BigDecimal sum = new BigDecimal("0");
        int count = stack.size();
        while (!stack.isEmpty()) {
            sum = sum.add(new BigDecimal(stack.pop().toString()));
        }
        return sum.divide(new BigDecimal(count), 7, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 求最大值
     *
     * @param stack
     * @return
     */
    public static BigDecimal maxFunction(Stack stack) {
        Double res =  Double.parseDouble(stack.pop().toString());
        while (!stack.isEmpty()) {
            res = Math.max(res.doubleValue() , Double.parseDouble(stack.pop().toString()));
        }
        return new BigDecimal(res);
    }

    /**
     * 求最大值
     *
     * @param stack
     * @return
     */
    public static BigDecimal minFunction(Stack stack) {
        Double res =  Double.parseDouble(stack.pop().toString());
        while (!stack.isEmpty()) {
            res = Math.min(res.doubleValue() , Double.parseDouble(stack.pop().toString()));
        }
        return new BigDecimal(res);
    }

    /**
     * 求绝对值
     *
     * @param stack
     * @return
     */
    public static BigDecimal absFunction(Stack stack) {
        Double res =  Double.parseDouble(stack.pop().toString());
        return new BigDecimal(Math.abs(res.doubleValue()));
    }

    /**
     * 判断相等  1 ： 相等 -1 ： 不等
     *
     * @param stack
     * @return
     */
    public static String equalsFunction(Stack stack){
        if(stack.size()!=2){
            return "-1";
        }
        String input1 = stack.pop().toString();
        String input2 = stack.pop().toString();
        return Objects.equals(input1,input2)? "1": "-1";
    }

    /**
     * 条件 第一个参数大于零则返回第二参数，否则返回第三个参数
     *
     * @param stack
     * @return
     */
    public static String ifFunction(Stack stack){
        if(stack.size()!=3){
            return "";
        }
        BigDecimal judge = new BigDecimal(stack.pop().toString());
        if(judge.doubleValue() <= 0.0){
            stack.pop();
        }
        return String.valueOf(stack.pop());
    }

    /**
     * 取反
     *
     * @param stack
     * @return
     */
    public static String notFunction(Stack stack){
        if(stack.size()!=1){
            return "0";
        }
        BigDecimal judge = new BigDecimal(stack.pop().toString());
        if(judge.doubleValue() < 0.0){
            return "1";
        }
        return judge.doubleValue() < 0.0? "1":"-1";
    }

    /**
     * And
     *
     * @param stack
     * @return
     */
    public static String andFunction(Stack stack){
        String res ="1";
        while (!stack.isEmpty()) {
            BigDecimal judge = new BigDecimal(stack.pop().toString());
            if(judge.doubleValue() <= 0.0 ){
                res = "-1";
                break;
            }
        }
        return res;
    }

    /**
     * OR
     *
     * @param stack
     * @return
     */
    public static String orFunction(Stack stack){
        String res ="-1";
        while (!stack.isEmpty()) {
            BigDecimal judge = new BigDecimal(stack.pop().toString());
            if(judge.doubleValue() > 0.0 ){
                res = "1";
                break;
            }
        }
        return res;
    }

    /**
     * OR
     *
     * @param stack
     * @return
     */
    public static BigDecimal stdevFunction(Stack stack){
        if(stack.size()<2){
            return new BigDecimal(0);
        }

        List<BigDecimal> valueList = new ArrayList<>();
        while (!stack.isEmpty()) {
            valueList.add(new BigDecimal(stack.pop().toString()));
        }

        BigDecimal sum = new BigDecimal(0);
        for(BigDecimal i : valueList){
            sum = sum.add(i);
        }
        BigDecimal avg = sum.divide(new BigDecimal(valueList.size()), 7, BigDecimal.ROUND_HALF_UP);
        sum= new BigDecimal(0);
        for(BigDecimal i : valueList){
            sum = sum.add(i.subtract(avg).multiply(i.subtract(avg)));
        }

        return new BigDecimal(Math.sqrt(sum.divide(new BigDecimal(valueList.size()-1), 7, BigDecimal.ROUND_HALF_UP).doubleValue()));

    }
}
