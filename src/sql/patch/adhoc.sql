-- YHAAO-15742 日常检查--批量删除检查配置
update nurse_site_maintain_config set deleted = false where deleted is null;
-- YHAAO-15804 是否允许下载模板控制
update audit_case set allow_download = true where allow_download is null;