---- X1000 ----
create view X1000 as
with recursive range(X) as (
  select 1 X
  union all
  select X + 1 from range limit 1000
)
select X from range
;


---- X1000000 ----
create or replace view X1000000 as
with recursive range(X) as (
  select 1 X
  union all
  select X + 1 from range limit 1000000
)
select X from range
;


---- EnsureNoTableOrViewMetaQuery ----
select case when type = 'view' then 'drop view if exists main.' || name
            when type = 'table' then 'drop table if exists main.' || name
            else null end as cmd
from sqlite_master
where lower(name) in (lower(?),lower(?),lower(?),lower(?))
union all
select case when type = 'view' then 'drop view if exists temp.' || name
            when type = 'table' then 'drop table if exists temp.' || name
            else null end as cmd
from sqlite_temp_master
where lower(name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapSchemaMetaQuery ----
select case when type = 'view' then 'drop view if exists main.' || name
            when type = 'table' then 'drop table if exists main.' || name
            when type = 'trigger' then 'drop trigger if exists main.' || name
            else null end as cmd
from sqlite_master
union all
select case when type = 'view' then 'drop view if exists temp.' || name
            when type = 'table' then 'drop table if exists temp.' || name
            when type = 'trigger' then 'drop trigger if exists temp.' || name
            else null end as cmd
from sqlite_temp_master
;
