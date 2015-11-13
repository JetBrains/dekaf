---- X1 ----
if object_id('X1') is null
execute('
    create view X1
    as
    select 1 as X
')
;

---- X10 ----
if object_id('X10') is null
execute('
    create view X10 as
    select 1 as X
    union all
    select 2 as X
    union all
    select 3 as X
    union all
    select 4 as X
    union all
    select 5 as X
    union all
    select 6 as X
    union all
    select 7 as X
    union all
    select 8 as X
    union all
    select 9 as X
    union all
    select 10 as X
')
;  
  
  
---- X1000 ----
if object_id('X1000') is null
execute('
    create view X1000 as
    select (A.X - 1) * 100 + (B.X - 1) * 10 + C.X as X
    from X10 A,
         X10 B,
         X10 C
')
;


---- X1000000 ----
if object_id('X1000000') is null
execute('
    create view X1000000 as
    select (P.X - 1)*1000 + Q.X as X
    from X1000 P,
         X1000 Q
')
;


---- EnsureNoForeignKeysMetaQuery ----
select 'alter table "' + T.name + '" drop constraint "' + C.name + '"'
from sysreferences R,
     sysobjects T,
     sysobjects C
where R.tableid = T.id
  and R.constrid = C.id
  and lower(T.name) in (lower(?),lower(?),lower(?),lower(?))
  and T.name not like 'sys%'
order by R.constrid desc
;

---- EnsureNoTableOrViewMetaQuery ----
select 'drop ' + case when type = 'U' then 'table'
                      when type = 'V' then 'view'
                      else 'unknown object' end
               + ' "' + name + '"' as cmd
from sysobjects
where type in ('U','V')
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
  and name not like 'sys%'
order by type desc, id desc
;


---- ZapForeignKeysMetaQuery ----
select 'alter table "' + T.name + '" drop constraint "' + C.name + '"'
from sysreferences R,
     sysobjects T,
     sysobjects C
where R.tableid = T.id
  and R.constrid = C.id
  and T.name not like 'sys%'
order by R.constrid desc
;

---- ZapSchemaMetaQuery ----
select 'drop ' + case when type = 'U' then 'table'
                      when type = 'V' then 'view'
                      else 'unknown object' end
               + ' "' + name + '"' as cmd
from sysobjects
where type in ('U','V')
  and name not like 'sys%'
order by type desc, id desc
;

