package com.ge.apm.dao.mapper.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SimplePartsInventoryMapper {

	@Select("<script>"
			+ "select COALESCE(sum(onhand_qty), 0)  from simple_parts_inventory where parts_item_id = #{partsItemId}  and parts_warehouse_id in "
			+ "<foreach item='item' index='index' collection='partsWarehouseIds' open='(' separator=', ' close=')'>" + "#{item}" +"</foreach>"
			+  "</script>")
	Integer getInventoryNumByPartsItem(@Param("partsItemId") String partsItemId, @Param("partsWarehouseIds") List<String> partsWarehouseIds);


	@Select("select pi.* from simple_parts_item pi, simple_parts_inventory inv where pi.id=inv.parts_item_id and inv.parts_warehouse_id = #{sourcePartsWarehouseId} and inv.parts_locator_id = #{sourcePartsWarehouseLocatorId} and inv.onhand_qty>0")
	List<SimplePartsItem> findPartsItemsByWarehouseIdAndLocatorId(@Param("sourcePartsWarehouseId") String sourcePartsWarehouseId, @Param("sourcePartsWarehouseLocatorId") String sourcePartsWarehouseLocatorId);

	@Select(value = "select * from simple_parts_inventory where  parts_warehouse_id = #{partsWareHouseId}")
	List<SimplePartsInventory> findPartsInventory(@Param("partsWareHouseId") String partsWareHouseId);





	@Select("<script>"
			+ "SELECT spi.*"
			+ "FROM simple_parts_inventory spi "
			+ "LEFT JOIN simple_parts_item spitem ON spi.parts_item_id = spitem.id "
			+ "<where>"
			+ "  <if test='partsWarehouseIds != null and partsWarehouseIds.size > 0'>"
			+ "    AND spi.parts_warehouse_id IN "
			+ "    <foreach item='item' index='index' collection='partsWarehouseIds' open='(' separator=',' close=')'>"
			+ "      #{item}"
			+ "    </foreach>"
			+ "  </if>"
			+ "  <if test='queryType == 1'>"
			+ "    AND ("
			+ "      spitem.maximum_quantity IS NOT NULL AND spitem.maximum_quantity &gt; 0 AND "
			+ "      spi.onhand_qty &gt;= spitem.maximum_quantity * 0.9"
			+ "    )"
			+ "  </if>"
			+ "  <if test='queryType == 2'>"
			+ "    AND ("
			+ "      spitem.minimum_quantity IS NOT NULL AND spitem.minimum_quantity &gt; 0 AND "
			+ "      spi.onhand_qty &lt;= spitem.minimum_quantity * 1.2"
			+ "    )"
			+ "  </if>"
			+ "  <if test='name != null'>"
			+ "   AND spi.name LIKE CONCAT('%', #{name}, '%')"
			+ "  </if>"
			+ "  <if test='tenantUID != null'>"
			+ "   AND spi.tenant_uid = #{tenantUID}"
			+ "  </if>"
			+ "</where>"
			+ "<if test='pageSize != null and offset != null'>"
			+ " LIMIT #{pageSize} OFFSET #{offset}"
			+ "</if>"
			+ "</script>")
	List<SimplePartsInventory> findPartsInventoryList(Map<String, Object> params   );

	@Select("<script>"
			+ "SELECT count(*) "
			+ "FROM simple_parts_inventory spi "
			+ "LEFT JOIN simple_parts_item spitem ON spi.parts_item_id = spitem.id "
			+ "<where>"
			+ "  <if test='partsWarehouseIds != null and partsWarehouseIds.size > 0'>"
			+ "    AND spi.parts_warehouse_id IN "
			+ "    <foreach item='item' index='index' collection='partsWarehouseIds' open='(' separator=',' close=')'>"
			+ "      #{item}"
			+ "    </foreach>"
			+ "  </if>"
			+ "  <if test='queryType == 1'>"
			+ "    AND ("
			+ "      spitem.maximum_quantity IS NOT NULL AND spitem.maximum_quantity &gt; 0 AND "
			+ "      spi.onhand_qty &gt;= spitem.maximum_quantity * 0.9"
			+ "    )"
			+ "  </if>"
			+ "  <if test='queryType == 2'>"
			+ "    AND ("
			+ "      spitem.minimum_quantity IS NOT NULL AND spitem.minimum_quantity &gt; 0 AND "
			+ "      spi.onhand_qty &lt;= spitem.minimum_quantity * 1.2"
			+ "    )"
			+ "  </if>"
			+ "  <if test='name != null'>"
			+ "   AND spi.name LIKE CONCAT('%', #{name}, '%')"
			+ "  </if>"
			+ "  <if test='tenantUID != null'>"
			+ "   AND spi.tenant_uid = #{tenantUID}"
			+ "  </if>"
			+ "</where>"
			+ "</script>")
	int findPartsInventoryCount(Map<String, Object> map);
}
