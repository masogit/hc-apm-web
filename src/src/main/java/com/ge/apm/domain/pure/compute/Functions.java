package com.ge.apm.domain.pure.compute;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Stack;

public class Functions {

    private static final int SCALE = 4;

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
            .put("power","powerFunction")
            .put("sqrt","sqrtFunction")
            .put("degrees","degreesFunction")
            .put("correl","correlFunction")
            .put("slope","slopeFunction")
            .put("intercept","interceptFunction")
            .put("atan","atanFunction")
            .put("tan","tanFunction")
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
        return sum.setScale(4, RoundingMode.HALF_UP);
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
        return sum.divide(new BigDecimal(count), SCALE, BigDecimal.ROUND_HALF_UP);
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
        return new BigDecimal(res).setScale(SCALE, RoundingMode.HALF_UP);
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
        return new BigDecimal(res).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 求绝对值
     *
     * @param stack
     * @return
     */
    public static BigDecimal absFunction(Stack stack) {
        Double res =  Double.parseDouble(stack.pop().toString());
        return new BigDecimal(Math.abs(res.doubleValue())).setScale(SCALE, RoundingMode.HALF_UP);
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
        BigDecimal avg = sum.divide(new BigDecimal(valueList.size()), SCALE, BigDecimal.ROUND_HALF_UP);
        sum= new BigDecimal(0);
        for(BigDecimal i : valueList){
            sum = sum.add(i.subtract(avg).multiply(i.subtract(avg)));
        }

        return new BigDecimal(Math.sqrt(sum.divide(new BigDecimal(valueList.size() - 1),SCALE, RoundingMode.HALF_UP).doubleValue()))
                .setScale(SCALE, RoundingMode.HALF_UP);

    }

    /**
     * same as Math.pow
     * @param stack
     * @return
     */
    public static BigDecimal powerFunction(Stack stack){
        if(stack.size()!=2){
            return new BigDecimal(0);
        }
        double base = Double.parseDouble(stack.pop().toString());
        double pow = Double.parseDouble(stack.pop().toString());
        return new BigDecimal(Math.pow(base, pow)).setScale(SCALE, RoundingMode.HALF_UP);
    }


    public static BigDecimal sqrtFunction(Stack stack){
        if(stack.size()!=1){
            return new BigDecimal(0);
        }
        return new BigDecimal(Math.sqrt(Double.parseDouble(stack.pop().toString()))).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal tanFunction(Stack stack){
        if(stack.size()!=1){
            return new BigDecimal(0);
        }
        return new BigDecimal(Math.tan(Double.parseDouble(stack.pop().toString()))).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal atanFunction(Stack stack){
        if(stack.size()!=1){
            return new BigDecimal(0);
        }
        return new BigDecimal(Math.atan(Double.parseDouble(stack.pop().toString()))).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal degreesFunction(Stack stack){
        if(stack.size()!=1){
            return new BigDecimal(0);
        }
        return new BigDecimal(Math.toDegrees(Double.parseDouble(stack.pop().toString()))).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 相关系数，参数为偶数，前一半X数组，后一半Y数组
     * @param stack
     * @return
     */
    public static BigDecimal correlFunction(Stack stack){
        if(stack.size() % 2 ==1){
            return new BigDecimal(0);
        }
        int length = stack.size()/2;
        double []x = new double[length];
        double []y = new double[length];
        for(int i= 0;i<length;i++){
            x[i] = Double.parseDouble(stack.pop().toString());
        }
        for(int i= 0;i<length;i++){
            y[i] = Double.parseDouble(stack.pop().toString());
        }
        double averageX = getAverage(x);
        double averageY = getAverage(y);

        double sum1 = 0, sum2 = 0, sum3 = 0;
        for(int i= 0;i<length;i++){
            sum1 += (x[i]-averageX)*(y[i]-averageY);
            sum2 += (x[i]-averageX)*(x[i]-averageX);
            sum3 += (y[i]-averageY)*(y[i]-averageY);
        }
        return new BigDecimal(sum1/Math.sqrt(sum2*sum3)).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 返回通过 known_y's 和 known_x's 中数据点的线性回归线的斜率。
     * 参数为偶数，前一半X数组，后一半Y数组
     * @param stack
     * @return
     */
    public static BigDecimal slopeFunction(Stack stack){
        if(stack.size() % 2 ==1){
            return new BigDecimal(0);
        }
        int length = stack.size()/2;
        double []x = new double[length];
        double []y = new double[length];
        for(int i= 0;i<length;i++){
            x[i] = Double.parseDouble(stack.pop().toString());
        }
        for(int i= 0;i<length;i++){
            y[i] = Double.parseDouble(stack.pop().toString());
        }
        double averageX = getAverage(x);
        double averageY = getAverage(y);
        double sum1 = 0, sum2 = 0;
        for(int i= 0;i<length;i++){
            sum1 += (x[i]-averageX)*(y[i]-averageY);
            sum2 += (y[i]-averageY)*(y[i]-averageY);
        }
        return new BigDecimal(sum1/sum2).setScale(SCALE, RoundingMode.HALF_UP);
    }


    /**
     * 参数为偶数，前一半X数组，后一半Y数组
     * @param stack
     * @return
     */
    public static BigDecimal interceptFunction(Stack stack){
        if(stack.size() % 2 ==1){
            return new BigDecimal(0);
        }
        int length = stack.size()/2;
        double []x = new double[length];
        double []y = new double[length];
        for(int i= 0;i<length;i++){
            x[i] = Double.parseDouble(stack.pop().toString());
        }
        for(int i= 0;i<length;i++){
            y[i] = Double.parseDouble(stack.pop().toString());
        }
        double averageX = getAverage(x);
        double averageY = getAverage(y);
        double sum1 = 0, sum2 = 0;
        for(int i= 0;i<length;i++){
            sum1 += (x[i]-averageX)*(y[i]-averageY);
            sum2 += (y[i]-averageY)*(y[i]-averageY);
        }
        return new BigDecimal(averageX-averageY*sum1/sum2).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private static double getAverage(double[] inputData) {
        if (ArrayUtils.isEmpty(inputData)) {
            return 0;
        }
        return getSum(inputData) / inputData.length;
    }

    private static double getSum(double[] inputData) {
        if (ArrayUtils.isEmpty(inputData)) {
            return 0;
        }
        return Arrays.stream(inputData).sum();
    }

}
