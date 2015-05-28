---- GenDeleteTablesOrViews ----
select cmd
from (
  with O as ( select object_name as name, object_id
              from sys.user_objects
              where upper(object_name) in (upper(?), upper(?), upper(?), upper(?)) )
  select 'drop view '||V.view_name as cmd, O.object_id
  from O join sys.user_views V on O.name = V.view_name
  union all
  select 'drop table '||T.table_name||' cascade constraints' as cmd, O.object_id
  from O join sys.user_tables T on O.name = T.table_name
  where not (nested = 'YES')
)
order by object_id desc
/

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


