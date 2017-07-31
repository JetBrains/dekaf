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
with Names as ( select lower(?) as name
                union
                select lower(?) as name
                union
                select lower(?) as name
                union
                select lower(?) as name ),
     Drop_FK as ( select 'alter table ' + T.name + ' drop constraint ' + quotename(rtrim(F.name)) as cmd,
                         1 as priority,
                         F.create_date
                  from sys.tables T,
                       sys.foreign_keys F
                  where F.parent_object_id = T.object_id
                    and F.schema_id = schema_id()
                    and lower(T.name) in (select name from Names)
                    and not T.is_ms_shipped = 1 ),
     Drop_Tables_or_Views as ( select 'drop ' + replace(replace(type collate database_default,'U','table'),'V','view') + ' ' + quotename(name) as cmd,
                                      2 as priority,
                                      create_date
                               from sys.objects
                               where type collate database_default in ('U','V')
                                 and lower(name) in (select name from Names)
                                 and parent_object_id = 0
                                 and not is_ms_shipped = 1 ),
     Drop_All as ( select * from Drop_FK
                   union all
                   select * from Drop_Tables_or_Views )
select cmd
from Drop_All
order by priority asc, create_date desc
;


---- ZapSchemaMetaQuery ----
with Objects as ( select type collate database_default as kind,
                         name,
                         create_date
                  from sys.objects
                  where schema_id = schema_id()
                    and type collate database_default in ('U','V','P','FN','IF','SN')
                    and parent_object_id = 0
                    and not is_ms_shipped = 1),
     Kinds as ( select 'U' as kind, 'table' as word
                union all
                select 'V' as kind, 'view' as word
                union all
                select 'P' as kind, 'procedure' as word
                union all
                select 'FN' as kind, 'function' as word
                union all
                select 'IF' as kind, 'function' as word
                union all
                select 'SN' as kind, 'synonym' as word ),
     Drop_FK as ( select 'alter table ' + quotename(T.name) + ' drop constraint ' + quotename(rtrim(F.name)) as cmd,
                         1 as priority,
                         F.create_date
                  from sys.tables T,
                       sys.foreign_keys F
                  where F.parent_object_id = T.object_id
                    and F.schema_id = schema_id()
                    and not T.is_ms_shipped = 1 ),
     Drop_Objects as ( select 'drop ' + word + ' ' + quotename(name) as cmd,
                              2 as priority,
                              create_date
                       from Objects join Kinds on Objects.kind = Kinds.kind ),
     Drop_Types as ( select 'drop type ' + quotename(name) as cmd,
                            3 as priority,
                            cast(null as datetime) as created_date
                     from sys.types
                     where is_user_defined = 1 ),
     Drop_All as ( select * from Drop_FK
                   union all
                   select * from Drop_Objects
                   union all
                   select * from Drop_Types )
select cmd
from Drop_All
order by priority asc, create_date desc
;

