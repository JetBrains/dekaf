---- TableOrViewExistence ----
select top 1 1
from sysobjects
where name = ?
  and type in ('U','V')


---- X1 ----
create table X1 (X bit not null);
insert into X1 values (1);


---- X10 ----
create table X10 (X int not null);

insert into X10 values (1);
insert into X10 values (2);
insert into X10 values (3);
insert into X10 values (4);
insert into X10 values (5);
insert into X10 values (6);
insert into X10 values (7);
insert into X10 values (8);
insert into X10 values (9);
insert into X10 values (10);

  
  
---- X1000 ----
create table X1000 (X int not null);

insert into X1000 (X)
       select (A.X - 1) * 100 + (B.X - 1) * 10 + C.X as X
       from X10 A,
            X10 B,
            X10 C
;


---- X1000000 ----
if object_id('X1000000') is null
execute('
    create view X1000000 as
    select (P.X - 1)*1000 + Q.X as X
    from X1000 P,
         X1000 Q
')



---- EnsureNoForeignKeysMetaQuery ----
select 'alter table "' + T.name + '" drop constraint "' + C.name + '"'
from sysreferences R,
     sysobjects T,
     sysobjects C
where R.tableid = T.id
  and R.constrid = C.id
  and lower(T.name) in (lower(?),lower(?),lower(?),lower(?))
  and T.name not like 'sys%'
order by R.constrid desc
;

---- EnsureNoTableOrViewMetaQuery ----
select 'drop ' + case when type = 'U' then 'table'
                      when type = 'V' then 'view'
                      else 'unknown object' end
               + ' "' + name + '"' as cmd
from sysobjects
where type in ('U','V')
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
  and name not like 'sys%'
order by type desc, id desc
;


---- ZapForeignKeysMetaQuery ----
select 'alter table "' + T.name + '" drop constraint "' + C.name + '"'
from sysreferences R,
     sysobjects T,
     sysobjects C
where R.tableid = T.id
  and R.constrid = C.id
  and T.name not like 'sys%'
order by R.constrid desc
;

---- ZapSchemaMetaQuery ----
select 'drop ' + case when type = 'U' then 'table'
                      when type = 'V' then 'view'
                      when type = 'P' then 'procedure'
                      when type = 'SF' then 'function'
                      else 'unknown object' end
               + ' "' + name + '"' as cmd
from sysobjects
where type in ('U','V','P','SF')
  and name not like 'sys%'
order by type desc, id desc
;

