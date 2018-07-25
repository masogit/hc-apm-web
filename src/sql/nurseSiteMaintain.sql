CREATE INDEX nurse_site_maintain_item_index
    ON public.nurse_site_maintain_item USING btree
    (schedule_date ASC NULLS LAST)
    TABLESPACE pg_default;

ALTER TABLE public.nurse_site_maintain_item
    CLUSTER ON nurse_site_maintain_item_index;

 create or replace function tonytest
(   
    tablename text,
    out returnvalue text
) as $BODY$
declare
    myrecord RECORD;
    mysql text;
begin
    for myrecord in (SELECT tc.constraint_name FROM information_schema.table_constraints AS tc WHERE constraint_type = 'FOREIGN KEY' AND tc.table_name=''||tablename||'')
    loop
        mysql:=myrecord;
        mysql:=substring(mysql from 2 for char_length(mysql)-2);
        raise notice 'record=%',mysql;
        mysql:='ALTER TABLE '||tablename||' DROP CONSTRAINT '||mysql||'';
        execute mysql;
    end loop;
    returnvalue='success';
end;
$BODY$ language plpgsql;

select tonytest('nurse_site_maintain_config');
select tonytest('nurse_site_maintain_config_item');
select tonytest('nurse_site_maintain');
select tonytest('nurse_site_maintain_item');
select tonytest('nurse_site_maintain_data');

drop function tonytest(tablename text,OUT returnvalue text);

CREATE INDEX nurse_site_maintain_index
    ON public.nurse_site_maintain USING btree
    (schedule_date ASC NULLS LAST)
    TABLESPACE pg_default;

ALTER TABLE public.nurse_site_maintain
    CLUSTER ON nurse_site_maintain_index;

CREATE INDEX nurse_site_maintain_data_index
    ON public.nurse_site_maintain_data USING btree
    (id ASC NULLS LAST)
    TABLESPACE pg_default;