---- X1000 ----
create or replace view X1000 as
select generate_series as X
from generate_series(1,1000)
;


---- X1000000 ----
create or replace view X1000000 as
select generate_series as X
from generate_series(1,1000000)
;


---- EnsureNoTableOrViewMetaQuery ----
select case when table_type like 'VIEW' then concat('drop view if exists ', table_name)
            when table_type like '%TABLE' then concat('drop table if exists ', table_name, ' cascade')
            else null end as cmd
from information_schema.tables
where table_catalog = current_database()
  and table_schema = current_schema()
  and lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapSchemaMetaQuery ----
select concat('drop view if exists ', table_name) as cmd
from information_schema.views
where table_catalog = current_database()
  and table_schema = current_schema()
union all
select concat('drop table if exists ', table_name, ' cascade') as cmd
from information_schema.tables
where table_catalog = current_database()
  and table_schema = current_schema()
  and table_type ilike '%TABLE%'
;

