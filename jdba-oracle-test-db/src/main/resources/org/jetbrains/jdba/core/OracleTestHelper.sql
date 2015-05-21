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

