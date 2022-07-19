package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface ExportMapper {

    /**
     * 查询所有数据
     *
     * @param table 表名
     * @return list
     */
    @Select("select * from ${table}")
    List<Map<String, Object>> findAll(@Param("table") String table);

    /**
     * 根据传入的表和字段查询数据
     *
     * @param table 表名
     * @param field 查询条件字段
     * @param value 值
     * @return list
     */
    @Select("select * from ${table} where ${field} = #{value}")
    List<Map<String, Object>> findByField(@Param("table") String table, @Param("field") String field, @Param("value") String value);

    /**
     * 按照 tenant_uid导出
     *
     * @param table     表名
     * @param tenantUid tenantUid
     * @return list
     */
    @Select("select * from ${table} where tenant_uid = #{tenantUid}")
    List<Map<String, Object>> findByTenantUid(@Param("table") String table, @Param("tenantUid") String tenantUid);

    /**
     * 按照 asset_uid 关联 asset_info 的 tenant_uid 导出
     *
     * @param table     表名
     * @param tenantUid tenantUid
     * @return list
     */
    @Select("select a.* from ${table} a inner join asset_info b on a.asset_uid = b.uid where b.tenant_uid = #{tenantUid}")
    List<Map<String, Object>> findByAssetInfoTenantUid(@Param("table") String table, @Param("tenantUid") String tenantUid);

    /**
     * 按照 tableA.fieldA = tableB.fieldB where tableB.tenant_uid = ?
     *
     * @param tableA    查询表
     * @param tableB    关联表
     * @param fieldA    查询表字段
     * @param fieldB    关联表字段
     * @param tenantUid tenantUid
     * @return list
     */
    @Select("select a.* from ${tableA} a inner join ${tableB} b on a.${fieldA} = b.${fieldB} where b.tenant_uid = #{tenantUid}")
    List<Map<String, Object>> findByRelationTableTenantUid(@Param("tableA") String tableA, @Param("tableB") String tableB, @Param("fieldA") String fieldA, @Param("fieldB") String fieldB, @Param("tenantUid") String tenantUid);

    /**
     * 根据父表的关联表中 tenantUid 查询 : childTable.childField = parentTable.parentField and parentTable.relationTableField = relationTable.relationField where relationTable.tenant_uid = ?
     *
     * @param childTable          子表
     * @param parentTable         父表
     * @param childTableField     子表与父表关联字段
     * @param parentTableField    父表字段
     * @param parentRelationField 父表与关联表关联字段
     * @param relationTable       关联表
     * @param relationField       关联表字段
     * @param tenantUid           租户 id
     * @return list
     */
    @Select("select childTable.* from ${childTable} childTable inner join ${parentTable} parentTable on childTable.${childTableField} = parentTable.${parentTableField} " +
            "inner join ${relationTable} relationTable on parentTable.${parentRelationField} = relationTable.${relationField} where relationTable.tenant_uid = #{tenantUid}")
    List<Map<String, Object>> findByParentRelationTableTenantUid(@Param("childTable") String childTable, @Param("parentTable") String parentTable,
                                                                 @Param("childTableField") String childTableField, @Param("parentTableField") String parentTableField,
                                                                 @Param("parentRelationField") String parentRelationField, @Param("relationTable") String relationTable, @Param("relationField") String relationField,
                                                                 @Param("tenantUid") String tenantUid);

    /**
     * 根据表名和 where 条件获取去重后的某个值
     *
     * @param field    需要去重的字段
     * @param table    表名
     * @param sqlWhere where 条件
     * @return List<String>
     */
    @Select("select distinct ${field} from ${table} where ${sqlWhere}")
    List<String> distinct(@Param("field") String field, @Param("table") String table, @Param("sqlWhere") String sqlWhere);

    /**
     * 获取 mongo id
     *
     * @param boType  关联类型
     * @param boField 关联类型
     * @param uuids   关联 ids
     * @return mongo id 集合
     */
    @Select({"<script>" +
                "select object_storage_id from v2_blob_object where bo_type = #{boType} and ${boField} in " +
                "<foreach item = 'item' index = 'index' collection = 'uuids' open='(' separator=',' close=')'>" +
                    "#{item}" +
                "</foreach>" +
            "</script>"})
    List<String> fetchObjectIds(@Param("boType") String boType, @Param("boField") String boField, @Param("uuids") List<String> uuids);
}
