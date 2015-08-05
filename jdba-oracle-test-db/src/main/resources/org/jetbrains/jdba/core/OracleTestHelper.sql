---- X1000 ----
create or replace view X1000 as
with X10 as ( select 0 as D from dual
              union all
              select 1 as D from dual
              union all
              select 2 as D from dual
              union all
              select 3 as D from dual
              union all
              select 4 as D from dual
              union all
              select 5 as D from dual
              union all
              select 6 as D from dual
              union all
              select 7 as D from dual
              union all
              select 8 as D from dual
              union all
              select 9 as D from dual )
select A.D * 100 + B.D * 10 + C.D +1 as X
from X10 A,
     X10 B,
     X10 C
/


---- X1000000 ----
create or replace view X1000000 as
select (P.X - 1)*1000 + Q.X as X
from X1000 P,
     X1000 Q
/


---- EnsureNoTableOrViewMetaQuery ----
select substr(cmd,1,90) as cmd
from (
  with O as ( select object_name as name, object_id
              from sys.user_objects
              where upper(object_name) in (upper(to_char(?)), upper(to_char(?)), upper(to_char(?)), upper(to_char(?))) )
  select 'drop view '||V.view_name as cmd, O.object_id
  from O join sys.user_views V on O.name = V.view_name
  union all
  select 'drop table '||T.table_name||' cascade constraints' as cmd, O.object_id
  from O join sys.user_tables T on O.name = T.table_name
  where not (nested = 'YES')
    and table_name not like 'BIN$%$_'
)
order by object_id desc
/

---- ZapSchemaCommand ----
declare
  type strings is table of varchar(160);
  commands strings;
  cmd varchar(160);
begin
  --
  select cmd
  bulk collect into commands
  from
    (
    select 'drop sequence "'||sequence_name||'"' as cmd,
           1 as ord, 0 as rnum
    from user_sequences
    union all
    select 'drop type "'||type_name||'" force' as cmd,
           2 as ord, 0 as rnum
      from user_types
    union all
    select 'drop table "'||object_name||'" cascade constraints' as cmd,
           3 as ord, object_id as rnum
      from user_objects
      where object_type = 'TABLE'
        and object_name not like 'BIN$%$_'
    union all
    select 'drop view "'||view_name||'"' as cmd,
           4 as ord, rnum
      from user_views
        natural join
        (select object_name as view_name, object_id as rnum from user_objects where object_type = 'VIEW')
    union all
    select 'drop '||object_type||' '||object_name as cmd,
           5 as ord, object_id as rnum
       from user_objects
       where object_type in ('FUNCTION','PROCEDURE','PACKAGE','OPERATOR')
    union all
    select 'drop synonym '||object_name as cmd,
           9 as ord, object_id as rnum
       from user_objects
       where object_type = 'SYNONYM'
    )
  order by ord desc, rnum desc;
  --
  if commands.count > 0 then
    for i in commands.first .. commands.last
      loop
        cmd := commands(i);
        execute immediate cmd;
      end loop;
  end if;
  --
end;
/


