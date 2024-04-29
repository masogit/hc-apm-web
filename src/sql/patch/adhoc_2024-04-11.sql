CREATE index IF NOT exists inventory_order_detail_tenant_uid_idx ON public.inventory_order_detail (tenant_uid);
CREATE INDEX IF NOT exists inventory_order_detail_asset_id_idx ON public.inventory_order_detail (asset_id);
CREATE INDEX IF NOT exists inventory_issue_tenant_uid_idx ON public.inventory_issue (tenant_uid);
CREATE INDEX IF NOT exists inventory_issue_asset_id_idx ON public.inventory_issue (asset_id);