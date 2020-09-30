package com.ge.apm.dao.mapper.simpleparts;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

public interface SimplePartsTransactionMapper {


	@Select("<script>"
			+ "select COALESCE(sum(total_prices) / sum(transaction_qty), 0) from simple_parts_transaction where parts_item_id = #{partsItemId} and transaction_type = 0 and parts_warehouse_id in "
			+ "<foreach item='item' index='index' collection='partsWarehouseIds' open='(' separator=', ' close=')'>" + "#{item}" +"</foreach>"
			+  "</script>")
	BigDecimal getAvgPriceByPartsItem(@Param("partsItemId") String partsItemId, @Param("partsWarehouseIds") List<String> partsWarehouseIds);
}
