package com.ge.apm.dao.mapper.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
}
