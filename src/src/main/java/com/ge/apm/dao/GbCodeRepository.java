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
	@Query(value="select gb.* from gb_code gb where code=?",nativeQuery = true)
	public GbCode getGbByCode(String code);

	@Query(value="select gb.* from gb_code gb where level=?1",nativeQuery = true)
	public List<GbCode> getSubGb(int level);

	@Query(value="select gb.* from gb_code gb where code=?1",nativeQuery = true)
	public GbCode getGbCode(String Code);


	/*@Query(value = "select distinct a.name from gb_code a where a.tenant_uid=?1 and a.name like concat(?2,'%') order by a.name fetch first 20 rows only", nativeQuery = true)*/
	@Query(value = "select concat(a.code,'/', a.name) from gb_code a where a.code like concat(?1,'%') order by a.name fetch first 20 rows only", nativeQuery = true)
	public List<String> findGbCodeByPrefix(String prefix);

/*
--获取三级目录
select * from gb_code where (code between 3220100+1 and 3220100+99) and level =2
select * from gb_code where (code between 3220200+1 and 3220200+99) and level =2
--获取二级目录
select * from gb_code where (code between 3220000 and 3220000+9999) and level =1*/
}
