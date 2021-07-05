update zkb_contract c set quota_deduct_type = 'Old'  where c.start_date <'2021-03-01';
update zkb_contract c set quota_deduct_type = 'New'  where c.start_date >='2021-03-01';