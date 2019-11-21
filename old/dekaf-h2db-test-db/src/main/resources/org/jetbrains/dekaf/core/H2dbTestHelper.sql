---- X1000 ----
create or replace view X1000 as
select X
from system_range(1,1000)
;


---- X1000000 ----
create or replace view X1000000 as
select X
from system_range(1,1000000)
;


---- EnsureNoTableOrViewMetaQuery ----
select case when table_type like 'VIEW' then concat('drop view if exists "', replace(table_name, '"', '""'), '" cascade')
            when table_type like '%TABLE' then concat('drop table if exists "', replace(table_name, '"', '""'), '" cascade')
            else null end as cmd
from information_schema.tables
where table_schema = schema()
  and lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapSchemaMetaQuery ----
select concat('drop view if exists "', replace(table_name, '"', '""'), '" cascade') as cmd
from information_schema.views
where table_schema = schema()
union all
select concat('drop table if exists "', replace(table_name, '"', '""'), '" cascade') as cmd
from information_schema.tables
where table_schema = schema()
  and table_type like '%TABLE%'
union all
select concat('drop alias if exists "', replace(alias_name, '"', '""'), '"') as cmd
from information_schema.function_aliases
where alias_schema = schema()
;


