-- YHAAO-15906 临床助手工单，需要可以填写数值型
alter table nurse_site_maintain_data alter COLUMN check_value type bool using check_value::bool;