package com.ge.apm.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import com.ge.apm.callback.DataDelVO;
import com.ge.healthcare.cn.common.cons.ApmServiceName;
import com.ge.healthcare.cn.common.core.message.WechatMsgEvent;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class MqUtils {

    public static final Logger log = LoggerFactory.getLogger(MqUtils.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToMq(MqContext mqContext) {
        try {
            rabbitTemplate.convertAndSend(mqContext.getExChange(), mqContext.getRoutingKey(), mqContext);
        } catch (Exception e) {
            log.error("send to mq error exchange:{},routingKey:{},message:{},e :", mqContext.getExChange(), mqContext.getRoutingKey(), mqContext.getJsonObject(), e);
            throw new RuntimeException("send mq error");
        }
    }

    public void sendStringToMq(MqContext mqContext) {
        try {
            rabbitTemplate.convertAndSend(mqContext.getExChange(), mqContext.getRoutingKey(), JSON.toJSONString(mqContext));
        } catch (Exception e) {
            log.error("send to mq error exchange:{},routingKey:{},message:{},e :", mqContext.getExChange(), mqContext.getRoutingKey(), mqContext.getJsonObject(), e);
            throw new RuntimeException("send mq error");
        }
    }

    /**
     * 物理数据删除发送接口哦
     *
     * @param object      数据对象
     * @param objectType  对象信息 表类型
     * @param userAccount 操作用户
     */
    public void sendPhysicalDataDel(Object object, String objectType, UserAccount userAccount, MqContext mqContex) {
        DataDelVO dataDelVO = new DataDelVO();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jsonData", dataDelVO);
        mqContex.setJsonObject(jsonObject);
        if (userAccount == null) {
            userAccount = UserContextService.getCurrentUserAccount();
        }
        dataDelVO.setOperator(userAccount.getLoginName());
        dataDelVO.setCreatedDate(new Date());
        dataDelVO.setObjectType(objectType);
        dataDelVO.setObjectValue(JSON.toJSONString(object));
        dataDelVO.setHospitalUID(userAccount.getHospitalUID());
        dataDelVO.setInstitutionUID(userAccount.getInstitutionUID());
        dataDelVO.setSiteUID(userAccount.getSiteUID());
        dataDelVO.setTenantUID(userAccount.getTenantUID());

        dataDelVO.setServiceName(mqContex.getServiceName());
        dataDelVO.setClassName(mqContex.getClassName());
        dataDelVO.setMethodName(mqContex.getMethodName());
        dataDelVO.setDescribe(mqContex.getDescribe());

        sendToMq(mqContex);
    }

    //设备新增assetInfo中id值设置为null
    public void sendAssetToMQ(AssetInfo asset, UserAccount userAccount, MqContext mqContext) {
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(asset, assetInfo);
        MqContext mqContextDTO = new MqContext();
        mqContextDTO.setTime(new Date());
        mqContextDTO.setType("asset");
        mqContextDTO.setRoutingKey(RabbitConfig.UPDATE_ROUTING_KEY);
        mqContextDTO.setExChange(RabbitConfig.ASSET_CHANGE);
        mqContextDTO.setClassName(mqContext.getClassName());
        mqContextDTO.setMethodName(mqContext.getMethodName());
        mqContextDTO.setServiceName(mqContext.getServiceName());
        mqContextDTO.setDescribe(mqContext.getDescribe());
        mqContextDTO.setLocale(mqContext.getLocale());
        mqContextDTO.setSource(mqContext.getSource());
        mqContextDTO.setModifySource(mqContext.getModifySource());
        JSONObject jsonObject = ObjectUtil.isNotEmpty(mqContext.getJsonObject()) ? mqContext.getJsonObject() : new JSONObject();
        if (StringUtils.isNotBlank(mqContext.getModifySource())) {
            jsonObject.put("modifySource", mqContext.getModifySource());
        }
        jsonObject.put("currentUserAccount", userAccount);
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Field[] declaredFields = asset.getClass().getDeclaredFields();
        List<Field> fields1 = Arrays.asList(declaredFields);
        List<Field> fields2 = fields1.stream().filter(item -> {
            Column annotation = item.getAnnotation(Column.class);
            return annotation == null;
        }).collect(Collectors.toList());
        List<String> collect = fields2.stream().map(Field::getName).collect(Collectors.toList());
        filter.getExcludes().addAll(collect);
        String str = JSON.toJSONString(assetInfo, filter, SerializerFeature.WriteDateUseDateFormat,SerializerFeature.IgnoreNonFieldGetter);
        jsonObject.put("assetInfo", str);
        if(StringUtils.isNotBlank(assetInfo.getCreateType())){
            jsonObject.put("type",assetInfo.getCreateType());
        }
        mqContextDTO.setJsonObject(jsonObject);
        sendToMq(mqContextDTO);
    }


    public void sendUserAccessRecords(UserAccount userAccount,String businessType,String featureName){
        try {
            userAccount=userAccount!=null?userAccount:UserContextService.getCurrentUserAccount();
            JSONObject jsonObject = new JSONObject();
            UserAccount user = new UserAccount();
            user.setId(userAccount.getId());
            user.setName(userAccount.getName());
            user.setLoginName(userAccount.getLoginName());
            user.setUid(userAccount.getUid());
            user.setHospitalUID(userAccount.getHospitalUID());
            user.setTenantUID(userAccount.getTenantUID());
            user.setInstitutionUID(userAccount.getInstitutionUID());
            user.setSiteUID(userAccount.getSiteUID());
            jsonObject.put("user",user);
            jsonObject.put("featureName",featureName);
            jsonObject.put("businessType",businessType);
            MqContext build = MqContext.builder().jsonObject(jsonObject).exChange(RabbitConfig.ACCESS_RECORDS_CHANGE).routingKey(RabbitConfig.ACCESS_RECORDS_KEY).type("UserAccessRecords").time(new Date()).build();
            sendToMq(build);
        }catch (Exception e){
            log.error("sendUserAccessRecords error :",e);
        }
    }


    public void sendApprovalMessage(UserAccount userAccount,int businessType,String businessUid,Integer status){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("businessUid",businessUid);
            jsonObject.put("businessType",businessType);
            jsonObject.put("approvalStatus",status);
            UserAccount user = new UserAccount();
            user.setName(userAccount.getName());
            user.setId(userAccount.getId());
            user.setLoginName(userAccount.getLoginName());
            jsonObject.put("user",user);
            MqContext mqContext = MqContext.builder().time(new Date()).routingKey(RabbitConfig.APPROVAL_KEY).exChange(RabbitConfig.EVENT_NOTIFICATION_CHANGE).serviceName(ApmServiceName.WEB.getValue()).className("MultiLevelApprovalService").methodName("getMultiLevelApprovalPersonByAgree")
                    .jsonObject(jsonObject)
                    .build();
            sendToMq(mqContext);
        }catch (Exception e){
            log.error("sendApprovalMessage error :",e);
        }
    }
    public void sendWxMsgToMq(WechatMsgEvent wechatMsgEvent,String exChange,String routingKey1) {
        String routingKey = RabbitConfig.WX_MSG_ROUTING_KEY_PREFIX
                + wechatMsgEvent.getMsgEvent().name().replace("_", ".").toLowerCase();
        rabbitTemplate.convertAndSend(exChange, routingKey, wechatMsgEvent);
    }

}
