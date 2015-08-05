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
            where nspname = current_schema
            limit 1 )
--
select concat('drop ', what, ' if exists ', typname, ' cascade') as cmd,
       T.oid::varchar::bigint as ord
from pg_catalog.pg_type T
     natural join
     (values ('e'::"char", 'type'),('d'::"char", 'domain')) as TX(typtype, what)
where typnamespace = (select n_id from N)
--
union all
--
select concat('drop type if exists ', ST.typname, ' cascade') as cmd,
       ST.oid::varchar::bigint as ord
from pg_catalog.pg_type ST,
     pg_catalog.pg_class SC
where ST.typnamespace = (select n_id from N)
  and ST.typrelid = SC.oid
  and SC.relkind = 'c'::"char"
--
union all
--
select concat('drop ', what, ' if exists ', relname, ' cascade') as cmd,
       C.oid::varchar::bigint as ord
from pg_catalog.pg_class C
     natural join
     (values ('S'::"char", 'sequence'),('r'::"char", 'table'),('v'::"char", 'view')) as CX(relkind, what)
where relnamespace = (select n_id from N)
--
union all
--
select concat('drop function if exists ', proname, '(', oidvectortypes(proargtypes), ') cascade') as cmd,
       oid::varchar::bigint as ord
from pg_catalog.pg_proc
where pronamespace = (select n_id from N)
--
union all
--
select concat('drop operator if exists ', oprname, '(',
              coalesce(format_type(nullif(oprleft,0),null),'none'), ',',
              coalesce(format_type(nullif(oprright,0),null),'none'), ') cascade') as cmd,
       oid::varchar::bigint as ord
from pg_catalog.pg_operator
where oprnamespace = (select n_id from N)
--
order by ord desc
;

