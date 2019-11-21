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
  toRecycle integer;
begin
  --
  select cmd
  bulk collect into commands
  from
    (
    select 'drop sequence "'||sequence_name||'"' as cmd,
           1 as ord, 0 as rnum
    from sys.user_sequences
    where sequence_name not like 'ISEQ$$#_%' escape '#'
    union all
    select 'drop type "'||type_name||'" force' as cmd,
           2 as ord, 0 as rnum
      from sys.user_types
    union all
    select 'drop cluster "'||cluster_name||'"' as cmd,
           3 as ord, 0 as rnum
      from sys.user_clusters
    union all
    select 'drop table "'||object_name||'" cascade constraints' as cmd,
           4 as ord, object_id as rnum
      from sys.user_objects
      where object_type = 'TABLE'
        and object_name not like 'BIN$%$_'
        and object_name not like 'MLOG$#_%' escape '#'
        and object_name not in (select mview_name from sys.user_mviews where build_mode != 'PREBUILT')
    union all
    select 'drop materialized view "'||mview_name||'"' as cmd,
           5 as ord, rnum
      from ( select mview_name, nvl(object_id,999999999999999999) as rnum
             from sys.user_mviews M,
                  sys.user_objects O
             where M.mview_name = O.object_name (+)
               and 'MATERIALIZED VIEW' = O.object_type (+) )
    union all
    select 'drop view "'||view_name||'"' as cmd,
           6 as ord, rnum
      from sys.user_views
        natural join
        (select object_name as view_name, object_id as rnum from sys.user_objects where object_type = 'VIEW')
    union all
    select 'drop '||object_type||' "'||object_name||'"' as cmd,
           7 as ord, object_id as rnum
       from sys.user_objects
       where object_type in ('FUNCTION','PROCEDURE','PACKAGE')
    union all
    select 'drop '||object_type||' "'||object_name||'" force' as cmd,
           8 as ord, object_id as rnum
       from sys.user_objects
       where object_type in ('OPERATOR','INDEXTYPE')
    union all
    select 'drop synonym "'||object_name ||'"' as cmd,
           9 as ord, object_id as rnum
       from sys.user_objects
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
  select count(1)
    into toRecycle
    from user_objects
    where object_type in ('TABLE','SEQUENCE')
      and (object_name like 'BIN$%$_' or object_name like 'MLOG$#_%' escape '#' or object_name like 'ISEQ$$#_%' escape '#');
  --
  if toRecycle >= 1 then
    execute immediate 'purge recyclebin';
  end if;
  --
end;
/


