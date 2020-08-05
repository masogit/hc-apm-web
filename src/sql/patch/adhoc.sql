-- 2020.08.05 start 质控更新
update audit_default_summary set except_progress = 20 where level = 'A';
update audit_summary set except_progress = 20 where level = 'A';
-- 2020.08.05 end 质控更新