-- YHAAO-24071
ALTER TABLE v2_work_order ALTER COLUMN parent_wo_id TYPE VARCHAR(32);


-- YHAAO-24711
update shared_asset_req_detail  set clinical_dept_for_return_name='肝胆外一科' ,clinical_dept_for_return_uid ='19ac4ccc2fd84005a5523ca6e0abb0e8',clinical_dept_name='肝胆外一科',clinical_dept_uid='19ac4ccc2fd84005a5523ca6e0abb0e8' where id='2c9b858295666aac0195ad7b99d37fbf';
update shared_asset_request  set clinical_dept_name='肝胆外一科',clinical_dept_uid = '19ac4ccc2fd84005a5523ca6e0abb0e8' where id='2c9b858295666aac0195ad7b99d37fbe';
delete from  shared_asset_req_detail  where id='2c9b858295666aac0195ad7ba5407fc2';
delete from shared_asset_req_type where shared_id in ('2c9b858295666aac0195ad7b9c777fc0','2c9b858295d27afc0196149f277912e5');
delete from  shared_asset_request where id in ('2c9b858295666aac0195ad7b9c777fc0','2c9b858295d27afc0196149f277912e5');