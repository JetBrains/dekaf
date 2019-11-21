---- X1000 ----
create view if exists X1000 as
select number X from system.numbers limit 1000
;


---- X1000000 ----
create view if exists X1000000 as
select number X from system.numbers limit 1000000
;


---- EnsureNoTableOrViewMetaQuery ----
select 'drop table if exists `' || replaceAll(name, '`', '``') || '`' as cmd
  from system.tables
where database = currentDatabase()
  and lower(name) in (lower(?),lower(?),lower(?),lower(?))
;


---- ZapSchemaMetaQuery ----
select 'drop table if exists `' || replaceAll(name, '`', '`') || '`' as cmd
  from system.tables
where database = currentDatabase()
;
