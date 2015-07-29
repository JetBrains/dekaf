---- X1000 ----
create or replace view X1000 as
select generate_series as X
from generate_series(1,1000)
;


---- X1000000 ----
create or replace view X1000000 as
select generate_series as X
from generate_series(1,1000000)
;


---- EnsureNoTableOrViewMetaQuery ----
select case when table_type like 'VIEW' then concat('drop view if exists ', table_name)
            when table_type like '%TABLE' then concat('drop table if exists ', table_name, ' cascade')
            else null end as cmd
from information_schema.tables
where table_catalog = current_database()
  and table_schema = current_schema()
  and lower(table_name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapSchemaMetaQuery ----
with N as ( select min(oid) as n_id
            from pg_catalog.pg_namespace
            where nspname = current_schema )
--
select concat('drop sequence if exists ', relname, ' cascade') as cmd,
       1 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_class
where relnamespace = (select n_id from N)
  and relkind = 'S'::"char"
--
union all
--
select concat('drop type if exists ', typname, ' cascade') as cmd,
       2 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_type
where typtype = 'e'::"char"
--
union all
--
select concat('drop type if exists ', T.typname, ' cascade') as cmd,
       3 as priority, T.oid::varchar::bigint as ord
from pg_catalog.pg_type T,
     pg_catalog.pg_class C
where T.typnamespace = (select n_id from N)
  and T.typrelid = C.oid
  and C.relkind = 'c'::"char"
union all
--
select concat('drop domain if exists ', typname, ' cascade') as cmd,
       4 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_type
where typnamespace in (select n_id from N)
  and typtype = 'd'::"char"
--
union all
--
select concat('drop table if exists ', relname, ' cascade') as cmd,
       5 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_class
where relnamespace = (select n_id from N)
  and relkind = 'r'::"char"
--
union all
--
select concat('drop view if exists ', relname, ' cascade') as cmd,
       6 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_class
where relnamespace = (select n_id from N)
  and relkind = 'v'::"char"
--
union all
--
select concat('drop function if exists ', proname, '(', oidvectortypes(proargtypes), ') cascade') as cmd,
       7 as priority, oid::varchar::bigint as ord
from pg_catalog.pg_proc
where pronamespace = (select n_id from N)
--
order by priority desc, ord desc
;

