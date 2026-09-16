package com.ge.apm.view.transaction;

import com.ge.apm.dao.DeptTransactionLogRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.transaction.TransactionService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.DepTranForTrans;
import com.ge.apm.web.TransactionForm;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
public class DepTranDetailController extends JpaCRUDController<DeptTransactionLog> implements ServerEventInterface{

    @Getter @Setter
    private Date upStartTime;
    @Getter @Setter
    Double hours;
    @Getter @Setter
    private Integer times;
    @Getter @Setter
    private String remark;
    @Getter @Setter
    private Integer tmCategory;
    @Getter @Setter
    private Date startTime;
    @Getter @Setter
    private Date endTime;
    private TransactionService transactionService;
    private UserContextService userContextService;
    private UserAccountRepository userAccountRepository;
    @Setter @Getter
    private DeptTransactionLog deptTransactionLog;
    private Integer authorId;
    private BlobFileService blobFileService;
    @Getter @Setter
    private List<V2_BlobObject> transactionImages;
    @Getter @Setter
    private List<V2_BlobObject> viewImages;
    @Getter @Setter
    private StreamedContent templateFile;
    //    private ServerEventInterface callingController;
    @Setter @Getter
    private List<DeptTransactionLog> deptTranLogList;
    @Setter @Getter
    private ConfigCacheService configCacheService;
    @Getter @Setter
    TransactionStatis transactionStatis;
    @Override
    protected DeptTransactionLogRepository getDAO() {
        return deptDao;
    }
    DeptTransactionLogRepository deptDao;
    DepTranForTrans depTranForTrans;



    @Override
    protected void init(){
        depTranForTrans= new DepTranForTrans();
        transactionService = WebUtil.getBean(TransactionService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        authorId=userContextService.getLoginUser().getId();
        deptDao = WebUtil.getBean(DeptTransactionLogRepository.class);
        transactionImages = new ArrayList<>();
        blobFileService = WebUtil.getBean(BlobFileService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
    }
    public String titleNameDetail(){
        if(userContextService.isOnlyAssetStaff()){
            return "科务详情";
        }else{
            return "详情";
        }
    }
    private boolean search=false;
    public void searchRanageSet(){
        depTranForTrans= new DepTranForTrans();
        if(startTime != null){
            depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
        }
        if(endTime != null){
            depTranForTrans.setEndTime(TimeUtils.getInputEndDate(endTime));
        }
        search=true;

    }
    public void prepareEdit() {
        tmCategory=selected.getCategory();
        upStartTime=selected.getDate();
        hours= selected.getHours();
        times= selected.getTimes();
        remark=selected.getRemark();
        times=selected.getTimes();
        // 加载已有图片
        if(selected.getId()!=null){
            transactionImages = blobFileService.fetchBlobObject(selected.getId().toString(), V2_BlobObject.BO_TYPE_DeptTransactionLog, V2_BlobObject.BO_SUB_TYPE_IMG);
            if(transactionImages==null) transactionImages = new ArrayList<>();
        }

    }
    boolean isSummarySelected=false;
    @Override
    protected Page<DeptTransactionLog> loadData(PageRequest pageRequest) {
        PageImpl page=null;

        depTranForTrans.setPageNumber(pageRequest.getPageNumber());
        depTranForTrans.setPageSize(pageRequest.getPageSize());

        if(isSummarySelected){
            depTranForTrans.setUserId(transactionStatis.getAuthorId());
            depTranForTrans.setStartTime(transactionStatis.getStartTime());
            depTranForTrans.setEndTime(transactionStatis.getEndTime());
            deptTranLogList=refreshLoadDataForSelectedItem(depTranForTrans);
            isSummarySelected=false;
        }else{
            if(search && transactionStatis!=null){
//                if(transactionStatis!=null){
                depTranForTrans.setUserId(transactionStatis.getAuthorId());
                deptTranLogList=refreshLoadData(depTranForTrans);
                search=false;
//                }else{ //根据指定时间来查询
//                    depTranForTrans.setUserId(userContextService.getLoginUserId());
//                    depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
//                    depTranForTrans.setEndTime(TimeUtils.getInputStartDate(endTime));
//                }
            }else if(transactionStatis!=null){  //有外层查询条件，userId要与外层保持一致
                //修改数据后保刷新 分有搜寻时间 和无搜寻时间
                if(startTime ==null){
                    depTranForTrans.setUserId(transactionStatis.getAuthorId()==null ? userContextService.getLoginUserId() : transactionStatis.getAuthorId());
                    depTranForTrans.setStartTime(transactionStatis.getStartTime());
                    depTranForTrans.setEndTime(transactionStatis.getEndTime());
                    deptTranLogList=refreshLoadDataForSelectedItem(depTranForTrans);
                }else{
                    depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
                    depTranForTrans.setEndTime(TimeUtils.getInputStartDate(endTime));
                    depTranForTrans.setUserId(transactionStatis.getAuthorId()==null ? userContextService.getLoginUserId() : transactionStatis.getAuthorId());
                    deptTranLogList=refreshLoadData(depTranForTrans);
                }
            } else { //没有有外层查询条件
                //修改数据后保刷新 分有搜寻时间 和无搜寻时间
                if(startTime ==null){
                    depTranForTrans.setUserId(userContextService.getLoginUserId());
                    depTranForTrans.setStartTime(TimeUtils.getCurrentStartYYMM());
                    depTranForTrans.setStartTime(TimeUtils.getCurrentEndYYMM());
                    deptTranLogList=refreshLoadData(depTranForTrans);
                }else{
                    depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
                    depTranForTrans.setEndTime(TimeUtils.getInputStartDate(endTime));
                    depTranForTrans.setUserId(userContextService.getLoginUserId());
                    deptTranLogList=refreshLoadData(depTranForTrans);
                }
            }

        }
        page = new PageImpl(deptTranLogList,pageRequest,totalSize);
        return page;
    }


    public void getDetailsDataBySearch(){
        depTranForTrans.setPageNumber(0);
        depTranForTrans.setPageSize(50);
        depTranForTrans.setStartTime(TimeUtils.getCurrentStartYYMM());
        depTranForTrans.setEndTime(TimeUtils.getCurrentEndYYMM());
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(depTranForTrans,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        isSummarySelected=true;
        transactionStatis = (TransactionStatis)eventObject;
//        List<DeptTransactionLog> deptTransactionLogs = refreshLoadData(transactionStatis.getAuthorId());
    }
    Integer totalSize;
    /*userId 有两种情况
       1 当前登录的用户是非科室主任,那么userId就是当前登录的用户
       2 或者创建科务的用户id(这种情况是当登录用户以主任角登录后选择某个科务汇总表数据时传过来的创建科务的那个userId)*/
    public List<DeptTransactionLog> refreshLoadData(DepTranForTrans dep){
        if(startTime==null){
            dep.setStartTime(TimeUtils.getCurrentStartYYMM());
        }else{
            dep.setStartTime(TimeUtils.getStrYHMS(startTime,"YYYYMMDDHHMMSS"));
        }
        if(endTime==null){
            dep.setEndTime(TimeUtils.getCurrentEndYYMM());
        }else{
            dep.setEndTime(TimeUtils.getStrYHMS(endTime,"YYYYMMDDHHMMSS"));
        }
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(dep,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
        deptTranLogList= decomposeTransactionLog(trListByPerson);
        return deptTranLogList;
    }


    public void loadViewImages(DeptTransactionLog item) {
        if (item != null && item.getId() != null) {
            viewImages = blobFileService.fetchBlobObject(item.getId().toString(), V2_BlobObject.BO_TYPE_DeptTransactionLog, V2_BlobObject.BO_SUB_TYPE_IMG);
            if (viewImages == null) viewImages = new ArrayList<>();
        } else {
            viewImages = new ArrayList<>();
        }
    }

    public String getImageBase64(String objectStorageId) {
        return blobFileService.getImgBase64(objectStorageId);
    }
    public List<DeptTransactionLog> refreshLoadDataForSelectedItem(DepTranForTrans dep){
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(dep,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
        deptTranLogList= decomposeTransactionLog(trListByPerson);
        return deptTranLogList;
    }

    public void onselectDetailItem(){
        //    System.out.println(selected);
    }



    public void deleteKW(){
        deptDao.delete(selected);
        WebUtil.addSuccessMessage("科务已经成功删除");
    }




    private List<DeptTransactionLog>  decomposeTransactionLog(HashMap<String, Object> hmSO ){
        List<DeptTransactionLog> tranList = new ArrayList<DeptTransactionLog>();
        ArrayList al1 = (ArrayList) hmSO.get("data");
        for (int i=0;i<al1.size();i++){
            HashMap<String,Object> hm1 = (HashMap<String,Object>)al1.get(i);
            DeptTransactionLog ts = new DeptTransactionLog();
            ts.setId((Integer)hm1.get("id"));
            if(hm1.get("uid") != null) {
                ts.setUid(hm1.get("uid").toString());
            }
            if(hm1.get("authorName")!=null) {
                ts.setAuthorName(hm1.get("authorName").toString());
            }
            if(hm1.get("category")!=null) {
                ts.setCategory(Integer.valueOf(hm1.get("category").toString()));
            }
            if(hm1.get("clinicalDeptName")!=null) {
                ts.setClinicalDeptName(hm1.get("clinicalDeptName").toString());
            }
            if(hm1.get("hours")!=null) {
                ts.setHours(Double.valueOf(hm1.get("hours").toString()));
            }
            if(hm1.get("date")!=null) {
                Date date=null;
                String strDate=(String)hm1.get("date");
                if( strDate.length()>11)
                    date = TimeUtils.getTimeOnGMT(strDate);
                else{
                    date= TimeUtils.convertStrToDate(strDate);
                }


                ts.setDate(date);
            }
            if(hm1.get("remark")!=null) {
                ts.setRemark(hm1.get("remark").toString());
            }
            if(hm1.get("times")!=null) {
                ts.setTimes(Integer.valueOf(hm1.get("times").toString()));
            }
            ts.setAuthorId(Integer.valueOf(hm1.get("authorId").toString()));
            tranList.add(ts);
        }
        return tranList;
    }
    public void handleImageUpload(FileUploadEvent event) {
        if(transactionImages!=null && transactionImages.size()>=5){
            WebUtil.addErrorMessage("最多上传5张图片");
            return;
        }
        try {
            V2_BlobObject blobObject = blobFileService.uploadFile(event);
            if(blobObject!=null){
                blobObject.setBoType(V2_BlobObject.BO_TYPE_DeptTransactionLog);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
                if(selected!=null && selected.getId()!=null){
                    blobObject.setBoUuid(selected.getId().toString());
                }
                blobFileService.saveBlobObject(blobObject);
                if(transactionImages==null) transactionImages = new ArrayList<>();
                transactionImages.add(blobObject);
                WebUtil.addSuccessMessage(event.getFile().getFileName() + " 上传成功");
            }
        }catch (Exception e){
            WebUtil.addErrorMessage("图片上传失败");
        }
    }

    public void removeImage(V2_BlobObject image){
        try {
            blobFileService.deleteBlobFile(image);
            if(transactionImages!=null){
                transactionImages.remove(image);
            }
            WebUtil.addSuccessMessage("图片已删除");
        }catch (Exception e){
            WebUtil.addErrorMessage("图片删除失败");
        }
    }

    public boolean isImageUploadDisabled(){
        return transactionImages!=null && transactionImages.size()>=5;
    }
    public void prepareCreate(){
        remark=null;
        startTime=null;
        upStartTime=null;
        hours=null;
        times=null;
        tmCategory=null;
        transactionImages = new ArrayList<>();

    }
    public void locUpdateTransaction() {
        selected.setRemark(remark);
        selected.setDate(startTime);
        selected.setHours(hours);
        selected.setCategory(tmCategory);
        selected.setTimes(times);
        if(checkRepetition(userContextService.getLoginUser().getLoginName(),startTime,tmCategory, selected.getId())){
            WebUtil.addErrorMessage("重复数据，请检查");
            return;
        }
        transactionService.localUpdateTransaction(selected);
        WebUtil.addSuccessMessage("更新成功");
    }

    public boolean checkRepetition(String authorName,Date date,Integer category){
        return checkRepetition(authorName, date, category, null);
    }

    public boolean checkRepetition(String authorName,Date date,Integer category, Integer excludeId){
        // 重复校验：姓名 + 日期 + 类别 三者一致即为重复
        if (excludeId != null) {
            return deptDao.existsByAuthorNameAndDateAndCategoryExcludeId(authorName, date, category, excludeId) > 0;
        }
        return deptDao.existsByAuthorNameAndDateAndCategory(authorName, date, category) > 0;
    }
    public void locUpdateTransactionbyLink(){
        TransactionForm tf = new TransactionForm();
        tf.setCategory(tmCategory);
        tf.setRemark(remark);
        tf.setHours(hours);
        tf.setTimes(times);
        tf.setDate(TimeUtils.getStrYHMS(upStartTime,"YYYYMMDDHHMMSS"));
        if(checkRepetition(userContextService.getLoginUser().getLoginName(),upStartTime,tmCategory, selected.getId())){
            WebUtil.addErrorMessage("重复数据，请检查");
            return;
        }
        String result = transactionService.updateTransaction(tf,selected.getUid(),UserContextService.getAccessToken());
        if(result == null){
            WebUtil.addErrorMessage("更新失败，微服务调用异常，请稍后重试");
            return;
        }
        // 保存新上传的图片关联（boUuid为null的是本次新上传的，需要关联到当前记录）
        if(transactionImages!=null && selected.getId()!=null){
            for(V2_BlobObject img : transactionImages){
                if(img.getBoUuid()==null){
                    img.setBoUuid(selected.getId().toString());
                    blobFileService.saveBlobObject(img);
                }
            }
        }
        WebUtil.addSuccessMessage("更新成功");
    }

    public void createTransaction(){
        TransactionForm tf = new TransactionForm();
        tf.setCategory(tmCategory);
        tf.setRemark(remark);
        tf.setHours(hours);
        tf.setTimes(times);
        String date= TimeUtils.getStrYHMS(upStartTime,"YYYYMMDDHHMMSS");
        tf.setDate(date);
        if(checkRepetition(userContextService.getLoginUser().getLoginName(),upStartTime,tmCategory)){
            WebUtil.addErrorMessage("重复数据，请检查");
            return;
        }
        // 保存创建前的图片列表引用，cancel()会清空transactionImages
        List<V2_BlobObject> imagesToAssociate = transactionImages != null ? new ArrayList<>(transactionImages) : new ArrayList<>();
        LinkedHashMap<String, Object> createdData = transactionService.createTmByLink(tf, UserContextService.getAccessToken());

        // 关联图片到新创建的科务记录
        if(!imagesToAssociate.isEmpty()){
            String newIdStr = null;
            // 优先从微服务响应中获取ID
            if(createdData != null){
                Object newId = createdData.get("id");
                if(newId != null){
                    newIdStr = newId.toString();
                }
            }
            // 回退：从本地DB查询最新创建的记录
            if(newIdStr == null){
                DeptTransactionLog latest = deptDao.findLatestByAuthorNameAndDateAndCategory(
                    userContextService.getLoginUser().getLoginName(), upStartTime, tmCategory);
                if(latest != null && latest.getId() != null){
                    newIdStr = latest.getId().toString();
                }
            }
            if(newIdStr != null){
                for(V2_BlobObject img : imagesToAssociate){
                    if(img.getBoUuid() == null){
                        img.setBoUuid(newIdStr);
                        blobFileService.saveBlobObject(img);
                    }
                }
            }
        }

        WebUtil.addSuccessMessage("成功创建科务记录");
        cancel();
        deptTransactionLog= new DeptTransactionLog();
    }
    public void cancel(){
        this.remark=null;
        this.tmCategory=null;
        this.startTime=null;
        this.hours=null;
        this.times=null;
        this.transactionImages = new ArrayList<>();
    }

/*
    public void m1(ServerEventInterface sei, TransactionStatis tr){

    }

    public void onSelectSummary(TransactionStatis transactionStatis){
        System.out.println(transactionStatis);
    }*/





    public void handleImportUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        if (uploadedFile == null || uploadedFile.getFileName() == null) {
            WebUtil.addErrorMessage("请选择文件");
            return;
        }
        String fileName = uploadedFile.getFileName().toLowerCase();
        if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
            WebUtil.addErrorMessage("只支持xls或xlsx格式的文件");
            return;
        }

        try (Workbook workbook = WorkbookFactory.create(uploadedFile.getInputstream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() < 1) {
                WebUtil.addErrorMessage("文件中没有数据");
                return;
            }
            if ( sheet.getLastRowNum() > 101) {
                WebUtil.addErrorMessage("请分批次导入，单次限制100条");
                return;
            }

            // 解析表头，建立列索引映射
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = cell.getStringCellValue().trim();
                    headerMap.put(header, i);
                }
            }

            Integer nameIdx = headerMap.get("姓名");
            Integer dateIdx = headerMap.get("日期");
            Integer categoryIdx = headerMap.get("类别");
            Integer timesIdx = headerMap.get("次数");
            Integer hoursIdx = headerMap.get("工时");
            Integer remarkIdx = headerMap.get("备注");

            if (nameIdx == null || dateIdx == null || categoryIdx == null) {
                WebUtil.addErrorMessage("表头必须包含姓名、日期、类别列");
                return;
            }

            // 构建类别显示值→码值的双向映射
            List<I18nMessage> categoryList = FieldValueMessageController.getFieldValueList("transaction");
            Map<String, String> categoryNameToKey = new HashMap<>();
            Map<String, String> categoryKeyToName = new HashMap<>();
            for (I18nMessage msg : categoryList) {
                categoryNameToKey.put(msg.getValue(), msg.getMsgKey());
                categoryKeyToName.put(msg.getMsgKey(), msg.getValue());
            }

            UserAccount currentUser = userContextService.getLoginUser();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            int successCount = 0;
            int duplicateCount = 0;
            List<String> errors = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // 读取姓名
                    String authorName = getCellStringValue(row.getCell(nameIdx));
                    if (authorName == null || authorName.trim().isEmpty()) {
                        errors.add("第" + (i + 1) + "行: 姓名为空，已跳过");
                        continue;
                    }
                    UserAccount byName = userAccountRepository.getByName(authorName);
                    if (byName == null || !currentUser.getTenantUID().equals(byName.getTenantUID())) {
                        errors.add("第" + (i + 1) + "行: 姓名不存在，已跳过");
                        continue;
                    }

                    // 读取日期
                    Date date = parseDateCell(row.getCell(dateIdx), sdf);
                    if (date == null) {
                        errors.add("第" + (i + 1) + "行: 日期格式不正确，已跳过");
                        continue;
                    }

                    // 读取类别（显示值）并转换为码值
                    String categoryName = getCellStringValue(row.getCell(categoryIdx));
                    if (categoryName == null || categoryName.trim().isEmpty()) {
                        errors.add("第" + (i + 1) + "行: 类别为空，已跳过");
                        continue;
                    }
                    String categoryKey = categoryNameToKey.get(categoryName.trim());
                    if (categoryKey == null) {
                        errors.add("第" + (i + 1) + "行: 类别'" + categoryName + "'不存在，已跳过");
                        continue;
                    }
                    Integer category = Integer.valueOf(categoryKey);

                    // 重复校验：姓名 + 日期 + 类别 三者一致即为重复
                    if (deptDao.existsByAuthorNameAndDateAndCategory(authorName.trim(), date, category) > 0) {
                        duplicateCount++;
                        continue;
                    }

                    // 读取次数、工时、备注
                    Integer times = null;
                    if (timesIdx != null) {
                        String timesStr = getCellStringValue(row.getCell(timesIdx));
                        if (timesStr != null && !timesStr.trim().isEmpty()) {
                            times = (int) Double.parseDouble(timesStr.trim());
                        }
                    }

                    Double hours = null;
                    if (hoursIdx != null) {
                        String hoursStr = getCellStringValue(row.getCell(hoursIdx));
                        if (hoursStr != null && !hoursStr.trim().isEmpty()) {
                            hours = Double.parseDouble(hoursStr.trim());
                        }
                    }

                    String remark = null;
                    if (remarkIdx != null) {
                        remark = getCellStringValue(row.getCell(remarkIdx));
                    }
                    DeptTransactionLog deptTransactionLog = new DeptTransactionLog();
                    deptTransactionLog.setAuthorId(currentUser.getId());
                    deptTransactionLog.setAuthorLogin(currentUser.getLoginName());
                    deptTransactionLog.setAuthorName(authorName);
                    deptTransactionLog.setAuthorUID(currentUser.getUid());
                    deptTransactionLog.setTimes(times);
                    deptTransactionLog.setDate(date);
                    deptTransactionLog.setHours(hours);
                    deptTransactionLog.setCategory(category);
                    deptTransactionLog.setRemark(remark);
                    OrgInfo dept = configCacheService.findOrgInfoByUid(currentUser.getOrgInfoUID());
                    deptTransactionLog.setClinicalDeptId(dept.getId());
                    deptTransactionLog.setClinicalDeptUID(dept.getUid());
                    deptTransactionLog.setClinicalDeptName(dept.getName());
                    deptTransactionLog.setTenantUID(dept.getTenantUID());
                    deptTransactionLog.setInstitutionUID(dept.getInstitutionUID());
                    deptTransactionLog.setHospitalUID(dept.getHospitalUID());
                    deptTransactionLog.setSiteUID(dept.getSiteUID());
                    deptTransactionLog.setCreatedBy(currentUser.getLoginName());
                    deptTransactionLog.setCreatedDate(new Date());
                    transactionService.localUpdateTransaction(deptTransactionLog);
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }

            // 显示导入结果
            StringBuilder msg = new StringBuilder();
            msg.append("导入完成，成功: ").append(successCount).append("条");
            if (duplicateCount > 0) {
                msg.append("，重复跳过: ").append(duplicateCount).append("条");
            }
            if (!errors.isEmpty()) {
                msg.append("，失败: ").append(errors.size()).append("条");
            }
            WebUtil.addSuccessMessage(msg.toString());

            // 逐条显示错误详情
            for (String error : errors) {
                WebUtil.addErrorMessage(error);
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage("文件解析失败: " + e.getMessage());
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
                }
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal)) {
                    return String.valueOf((long) numVal);
                }
                return String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Date parseDateCell(Cell cell, SimpleDateFormat sdf) {
        if (cell == null) return null;
        try {
            if (cell.getCellTypeEnum() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            String dateStr = getCellStringValue(cell);
            if (dateStr == null || dateStr.trim().isEmpty()) return null;
            dateStr = dateStr.trim();
            // 支持 yyyy/MM/dd 和 yyyy-MM-dd 两种格式
            if (dateStr.contains("-") && !dateStr.contains("/")) {
                dateStr = dateStr.replace("-", "/");
            }
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        Sheet sheet = wb.getSheetAt(0);

        sheet.setColumnWidth(0, 256 * 20);
        sheet.setColumnWidth(1, 256 * 20);
        sheet.setColumnWidth(2, 256 * 20);
        sheet.setColumnWidth(3, 256 * 20);
        sheet.setColumnWidth(4, 256 * 20);
//        sheet.setDefaultRowHeightInPoints(30f);
    }
}