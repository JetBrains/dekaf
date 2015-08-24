---- X10 ----
create or replace view X10 as
select 1 as X
union all
select 2 as X
union all
select 3 as X
union all
select 4 as X
union all
select 5 as X
union all
select 6 as X
union all
select 7 as X
union all
select 8 as X
union all
select 9 as X
union all
select 10 as X
;  
  
  
---- X1000 ----
create or replace view X1000 as
select (A.X - 1) * 100 + (B.X - 1) * 10 + C.X as X
from X10 A,
     X10 B,
     X10 C
;


---- X1000000 ----
create or replace view X1000000 as
select (P.X - 1)*1000 + Q.X as X
from X1000 P,
     X1000 Q
;


---- EnsureNoTableOrViewMetaQuery ----
select 'drop ' + replace(replace(xtype,'U','table'),'V','view') + ' ' + name as cmd
from sysobjects
where xtype in ('U','V')
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
  and parent_obj = 0
order by id desc
;


---- ZapSchemaMetaQuery ----
select 'drop ' + replace(replace(xtype,'U','table'),'V','view') + ' ' + name as cmd
from sysobjects
where xtype in ('U','V')
  and parent_obj = 0
order by id desc
;

