---- X1 ----
create table X1 (X INT PRIMARY KEY)
;
INSERT INTO X1 (X) VALUES (1)
;

---- CreateDropFunction ----
CREATE OR REPLACE FUNCTION create_drop_statement(table_name text)
RETURNS NULL ON NULL INPUT
RETURNS text
  LANGUAGE java
AS $$ return "DROP TABLE IF EXISTS " + table_name; $$
;

---- DropDropFunction ----
DROP FUNCTION IF EXISTS create_drop_statement
;

---- ZapSchemaMetaQuery ----
SELECT create_drop_statement(table_name) as cmd
FROM system_schema.tables
where keyspace_name = ?;
;

---- EnsureNoTableOrViewMetaQuery ----
SELECT create_drop_statement(table_name) as cmd
FROM system_schema.tables
where keyspace_name = ? AND
table_name in (?, ?, ?, ?)
;
