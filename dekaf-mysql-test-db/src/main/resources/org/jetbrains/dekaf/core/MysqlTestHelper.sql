---- X10 ----
create or replace view X10 as
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
;  
  
  
---- X1000 ----
create or replace view X1000 as
select (A.X - 1) * 100 + (B.X - 1) * 10 + C.X as X
from X10 A,
     X10 B,
     X10 C
;


---- X1000000 ----
create or replace view X1000000 as
select (P.X - 1)*1000 + Q.X as X
from X1000 P,
     X1000 Q
;


---- EnsureNoForeignKeysMetaQuery ----
select concat('alter table ',table_name,' drop foreign key ',constraint_name) as command
from information_schema.table_constraints C
where C.constraint_type = 'FOREIGN KEY'
  and C.table_schema = schema()
  and lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
;

---- EnsureNoTableOrViewMetaQuery ----
select concat('drop ', object_type, ' if exists ', table_name, ' cascade') as cmd
     from (
       select 'table' as object_type, table_name
       from information_schema.tables
       where table_schema = schema()
         and table_type like '%TABLE'
       union
       select 'view' as object_type, table_name
       from information_schema.views
       where table_schema = schema()
       ) objects_to_drop
where lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapForeignKeysMetaQuery ----
select concat('alter table ',table_name,' drop foreign key ',constraint_name) as command
from information_schema.table_constraints C
where C.constraint_type = 'FOREIGN KEY'
  and C.table_schema = schema()
;

---- ZapSchemaMetaQuery ----
select concat('drop ', object_type, ' if exists ', object_name, ' ', suffix) as cmd
from (
  select 'table' as object_type, table_name as object_name, 'cascade' as suffix
  from information_schema.tables
  where table_schema = schema()
    and table_type like '%TABLE'
  union
  select 'view' as object_type, table_name as object_name, 'cascade' as suffix
  from information_schema.views
  where table_schema = schema()
  union
  select lower(routine_type) as object_type, routine_name as object_name, '' as suffix
  from information_schema.routines
  where routine_schema = schema()
  ) objects_to_drop
;
