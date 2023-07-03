-- YHAAO-18866
UPDATE pm_order_rule set period_unit = 'm' WHERE period_unit is null;