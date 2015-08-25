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
select 'alter table ' + T.name + ' drop constraint [' + rtrim(F.name) +']'
from sys.tables T,
     sys.foreign_keys F
where F.parent_object_id = T.object_id
  and F.schema_id = schema_id()
  and lower(T.name) in (lower(?),lower(?),lower(?),lower(?))
  and not T.is_ms_shipped = 1
order by T.create_date desc
;

---- EnsureNoTableOrViewMetaQuery ----
select 'drop ' + replace(replace(type collate database_default,'U','table'),'V','view') + ' ' + name as cmd
from sys.objects
where type collate database_default in ('U','V')
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
  and parent_object_id = 0
  and not is_ms_shipped = 1
order by create_date desc
;


---- ZapForeignKeysMetaQuery ----
select 'alter table ' + T.name + ' drop constraint [' + rtrim(F.name) +']'
from sys.tables T,
     sys.foreign_keys F
where F.parent_object_id = T.object_id
  and F.schema_id = schema_id()
  and not T.is_ms_shipped = 1
order by T.create_date desc
;

---- ZapSchemaMetaQuery ----
with Objects as ( select type collate database_default as type, name, create_date
                  from sys.objects
                  where schema_id = schema_id()
                    and type collate database_default in ('U','V','P','FN','SN')
                    and parent_object_id = 0
                    and not is_ms_shipped = 1),
     Types as ( select 'U' as type, 'table' as word
                union all
                select 'V' as type, 'view' as word
                union all
                select 'P' as type, 'procedure' as word
                union all
                select 'FN' as type, 'function' as word
                union all
                select 'SN' as type, 'synonym' as word )
select 'drop ' + word + ' ' + name as cmd
from Objects join Types on Objects.type = Types.type
order by create_date desc
;

