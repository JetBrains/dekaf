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


---- EnsureNoTableOrViewMetaQuery ----
select 'drop ' + replace(replace(xtype,'U','table'),'V','view') + ' ' + name as cmd
from sysobjects
where xtype in ('U','V')
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
  and parent_obj = 0
order by id desc
;


---- ZapForeignKeysMetaQuery ----
select 'alter table ' + T.name + ' drop constraint [' + rtrim(F.name) +']'
from sys.tables T,
     sys.foreign_keys F
where F.parent_object_id = T.object_id
  and F.schema_id = schema_id()
order by T.object_id desc
;

---- ZapSchemaMetaQuery ----
select 'drop ' + replace(replace(xtype,'U','table'),'V','view') + ' ' + name as cmd
from sysobjects
where xtype in ('U','V')
  and parent_obj = 0
order by id desc
;

