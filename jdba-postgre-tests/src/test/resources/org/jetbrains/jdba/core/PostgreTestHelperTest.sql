---- existence_of_type ----
select 1
from pg_catalog.pg_type T,
     pg_catalog.pg_namespace N
where T.typnamespace = N.oid
  and N.nspname = current_schema()
  and T.typname = ?
;

---- existence_of_class ----
select 1
from pg_catalog.pg_class C,
     pg_catalog.pg_namespace N
where C.relnamespace = N.oid
  and N.nspname = current_schema()
  and C.relname = ?
;

---- existence_of_proc ----
select 1
from pg_catalog.pg_proc P,
     pg_catalog.pg_namespace N
where P.pronamespace = N.oid
  and N.nspname = current_schema()
  and P.proname = ?
;

