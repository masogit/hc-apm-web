package com.ge.apm.service.zkb;


import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.domain.ZkbCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import java.util.List;

@Component
public class ZkbCourseService {

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbCourseRepository courseDao;

    public ZkbCourseRepository getCourseDao() {
        return courseDao;
    }

    public Page<ZkbCourse> findBySearchFilter(List<SearchFilter> searchFilters, PageRequest pageRequest) {

        return courseDao.findBySearchFilter(searchFilters,pageRequest);
    }

    public boolean isCourseExist(ZkbCourse selected) {
        List<ZkbCourse> courseList = courseDao.find(selected.getCid());
        ZkbCourse course = courseDao.findFirstByCid(selected.getCid());
        try {
            if(!course.getId().equals(selected.getId()))
                return true;
        } catch (Exception e) {
            System.out.println("NPE");;
        }
        /*if(courseList!=null && courseList.size()>0){
            for(ZkbCourse c:courseList){

            }
        }*/
        return false;
    }

    public ZkbCourse findFirstByCid(String cid){
        return courseDao.findFirstByCid(cid);
    }

    public ZkbCourse findFirstByCidAndCname(String cid,String cname){
        return courseDao.findFirstByCidAndCname(cid,cname);
    }

    public List<ZkbCourse> findByCname(String cname){
        return courseDao.findByCname(cname);
    }

    public boolean isContractUsed(ZkbCourse course) {

        /*Integer count = courseDao.findContractByCourseId(course.getCid());
        if(count!=null && count>0){
            return true;
        }*/

        if(course.getCid().equals("GEHC-Seno (ZKB)")) {

            return true;
        }

        return false;
    }
}
