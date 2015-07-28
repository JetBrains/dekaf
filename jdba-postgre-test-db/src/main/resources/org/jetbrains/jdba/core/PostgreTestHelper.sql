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
select concat('drop sequence if exists ', sequence_name) as cmd,
       1 as priority
from information_schema.sequences
where sequence_catalog = current_database()
  and sequence_schema = current_schema()
--
union all
--
select concat('drop type if exists ', typname, ' cascade') as cmd,
       2 as priority
from pg_catalog.pg_type
where typtype = 'e'::"char"
--
union all
--
select concat('drop type if exists ', T.typname, ' cascade') as cmd,
       3 as priority
from pg_catalog.pg_type T,
     pg_catalog.pg_namespace N,
     pg_catalog.pg_class C
where T.typnamespace = N.oid
  and N.nspname = current_schema()
  and T.typrelid = C.oid
  and C.relkind = 'c'::"char"
union all
--
select concat('drop domain if exists ', domain_name) as cmd,
       4 as priority
from information_schema.domains
where domain_catalog = current_database()
  and domain_schema = current_schema()
--
union all
--
select concat('drop table if exists ', table_name, ' cascade') as cmd,
       5 as priority
from information_schema.tables
where table_catalog = current_database()
  and table_schema = current_schema()
  and table_type ilike '%TABLE%'
--
union all
--
select concat('drop view if exists ', table_name) as cmd,
       6 as priority
from information_schema.views
where table_catalog = current_database()
  and table_schema = current_schema()
--
union all
--
select concat('drop function if exists ', P.proname, '(', oidvectortypes(P.proargtypes), ')') as cmd,
       7 as priority
from pg_catalog.pg_proc P,
     pg_catalog.pg_namespace N
where P.pronamespace = N.oid
  and N.nspname = current_schema()
--
order by priority desc
;

