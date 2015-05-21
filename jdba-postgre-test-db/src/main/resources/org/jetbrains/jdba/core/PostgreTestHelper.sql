---- GenDeleteTablesOrViews ----
select concat('drop table ', table_name)
from information_schema.tables
where table_catalog = current_database()
  and table_schema = current_schema()
  and lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
