---- X1 ----
create table X1 (X INT PRIMARY KEY)
;

---- X1000 ----
create table X1000 (X INT PRIMARY KEY)
;

---- X1000000 ----
create table X1000000 (X INT PRIMARY KEY)
;

---- CreateDropFunction ----
CREATE OR REPLACE FUNCTION create_drop_statement(kind text, table_name text)
RETURNS NULL ON NULL INPUT
RETURNS text
  LANGUAGE java
AS $$ return "DROP " + kind + " IF EXISTS " + table_name; $$
;

---- DropDropFunction ----
DROP FUNCTION IF EXISTS create_drop_statement
;

---- ZapViewsMetaQuery ----
SELECT keyspace_name_placeholder.create_drop_statement('MATERIALIZED VIEW', view_name) as cmd
FROM system_schema.views
where keyspace_name = 'keyspace_name_placeholder';
;

---- ZapTablesMetaQuery ----
SELECT keyspace_name_placeholder.create_drop_statement('TABLE', table_name) as cmd
FROM system_schema.tables
where keyspace_name = 'keyspace_name_placeholder';
;

---- EnsureNoViewMetaQuery ----
SELECT keyspace_name_placeholder.create_drop_statement('MATERIALIZED VIEW', view_name) as cmd
FROM system_schema.views
where keyspace_name = 'keyspace_name_placeholder' AND
view_name in (?, ?, ?, ?)
;

---- EnsureNoTableMetaQuery ----
SELECT keyspace_name_placeholder.create_drop_statement('TABLE', table_name) as cmd
FROM system_schema.tables
where keyspace_name = 'keyspace_name_placeholder' AND
table_name in (?, ?, ?, ?)
;
