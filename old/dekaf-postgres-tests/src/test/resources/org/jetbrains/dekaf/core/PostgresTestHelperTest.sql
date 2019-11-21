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

---- existence_of_operator ----
select 1
from pg_catalog.pg_operator O,
     pg_catalog.pg_namespace N
where O.oprnamespace = N.oid
  and N.nspname = current_schema()
  and O.oprname = ?
;

---- existence_of_collation ----
select 1
from pg_catalog.pg_collation C,
     pg_catalog.pg_namespace N
where C.collnamespace = N.oid
  and N.nspname = current_schema()
  and C.collname = ?



---- create_mater_view ----
create table x_order
(
    id int not null,
    city_id int not null,
    note varchar(1000)
)
;

create materialized view x_order_stat
as
select city_id, count(*) as frequency
from x_order
group by city_id
;


---- create_aggregate_1 ----
create aggregate avg1 (float8)
(
    sfunc = float8_accum,
    stype = float8[],
    finalfunc = float8_avg,
    initcond = '{0,0,0}'
);
