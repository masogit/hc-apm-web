package com.ge.apm.dao;

import com.ge.apm.domain.GbCode;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by lsg on 30/11/2017.
 */
public interface GbCodeRepository extends GenericRepository<GbCode> {


@Query(value="select gb.* from gb_code gb where code",nativeQuery = true)
	public List<GbCode> findGbByRange(Integer codeS,Integer codeE,int level);

	@Query(value="select gb.* from gb_code gb where level=0",nativeQuery = true)
	public List<GbCode> findGbRoot();


	@Query(value="select gb.* from gb_code gb where level=?1",nativeQuery = true)
	public List<GbCode> getSubGb(int level);
/*
--获取三级目录
select * from gb_code where (code between 3220100+1 and 3220100+99) and level =2
select * from gb_code where (code between 3220200+1 and 3220200+99) and level =2
--获取二级目录
select * from gb_code where (code between 3220000 and 3220000+9999) and level =1*/
}
