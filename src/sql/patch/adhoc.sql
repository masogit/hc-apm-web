-- YHAAO-23998 默认修改供应商用户为管理员
update vender_info set is_admin = true where is_admin is false;
