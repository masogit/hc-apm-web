package com.ge.apm.service.api;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import javaslang.Tuple;
import javaslang.Tuple6;
import javaslang.control.Option;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrgService {
  private static final Logger log = LoggerFactory.getLogger(CommonService.class);
  private Database db;

  @Resource(name = "connectionProvider")
  private ConnectionProvider connectionProvider;

  @PostConstruct
  public void init() {
    db = Database.from(connectionProvider);
  }

  @Autowired
  private OrgInfoRepository orgDao;


  public Tuple6<Integer, Integer, Integer, Integer, String, String> find(Integer id) {
    return db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where id = :id")
      .parameter("id", id)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache().toBlocking().single();
  }

  public Iterable<Tuple6<Integer, Integer, Integer, Integer, String, String>> find(Integer site, Integer hospital, Integer start, Integer limit) {
    return db.select("select id, parent_id as parent, site_id as site, hospital_id as hospital, name as cn, name_en as en from org_info where parent_id is not null and site_id = :site and hospital_id = :hospital offset :start limit :limit")
      .parameter("site", site).parameter("hospital", hospital).parameter("start", start).parameter("limit", limit)
      .getAs(Integer.class, Integer.class, Integer.class, Integer.class, String.class, String.class)
      .map(t -> Tuple.of(t._1(), Option.of(t._2()).getOrElse(0), Option.of(t._3()).getOrElse(0), Option.of(t._4()).getOrElse(0), Option.of(t._5()).getOrElse(""), Option.of(t._6()).getOrElse("")))
      .cache().toBlocking().toIterable();
  }

  public List<Map<String,String>> getAllOrg(String tenantUID){
    List<OrgInfo> tempInstitutionList = orgDao.findByTenantUIDAndOrgType(tenantUID,1);
    List<Map<String,String>> tempOrgMapList = new ArrayList<>();
    Map<String,String> tempOrgMap = null;
    for (OrgInfo oi : tempInstitutionList) {
      tempOrgMap = new HashMap<>();
      tempOrgMap.put("id", oi.getId().toString());
      tempOrgMap.put("name", oi.getName());
      tempOrgMapList.add(tempOrgMap);

      List<OrgInfo> tempHospitalList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(oi.getUid(),2);
      for (OrgInfo oii : tempHospitalList) {
        tempOrgMap = new HashMap<>();
        tempOrgMap.put("id", oii.getId().toString());
        tempOrgMap.put("name", oi.getName() + "/" + oii.getName());
        tempOrgMapList.add(tempOrgMap);

        List<OrgInfo> tempSiteList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(oii.getUid(),3);
        for (OrgInfo oiii : tempSiteList) {
          tempOrgMap = new HashMap<>();
          tempOrgMap.put("id", oiii.getId().toString());
          tempOrgMap.put("name", oi.getName() + "/" + oii.getName() + "/" + oiii.getName());
          tempOrgMapList.add(tempOrgMap);
        }
      }

    }

    return tempOrgMapList;
  }

  public Map<String, List<Map<String,Object>>> getDept(Integer orgId){
    Map<String, List<Map<String,Object>>> tempOrgMap = new HashMap<>();
    List<OrgInfo> tempOrgInfoList = null;
    if(orgId != null){
      OrgInfo tempOrgInfo = orgDao.findById(orgId);
      if(tempOrgInfo != null){
        tempOrgInfoList = orgDao.findBySiteUIDAndOrgTypeOrderByNameAsc(tempOrgInfo.getUid(),4);
        Map<String,Object> orgMap = null;
        List<Map<String,Object>> orgInfoMapList = new ArrayList<>();
        for (OrgInfo oi : tempOrgInfoList) {
          orgMap = new HashedMap();
          orgMap.put("id", oi.getId());
          orgMap.put("parent_id", oi.getHospitalId());
          orgMap.put("site_id", oi.getSiteId());
          orgMap.put("hospital_id", oi.getHospitalId());
          orgMap.put("name", oi.getName());
          orgMap.put("name_en", oi.getNameEn());
          orgInfoMapList.add(orgMap);
        }
        tempOrgMap.put("orgInfos", orgInfoMapList);
      }
    }
    return tempOrgMap;
  }

}
