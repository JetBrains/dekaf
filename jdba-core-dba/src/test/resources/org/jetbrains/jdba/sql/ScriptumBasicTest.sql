---- TheCommand ----

The Command


---- TheCommand + Oracle ----

The Oracle Command


---- TheCommand_semicolon_sameString ----

The Command;


---- TheCommand_semicolon_sameString_2 ----

The Command; ;


---- TheCommand_semicolon_nextString ----

The Command
;

---- TheCommand_semicolon_nextString_2 ----

The Command
;

;


---- TheCommand_slash_nextString ----

The Command
/

---- TheCommand_slash_nextString_2 ----

The Command
/

/


---- BasicCommand ----

insert
into basic_table
values (1,2,3)
;


---- PLBlock1 ----

decalre
  n natural;
begin
  select count(ditinct owner) into n from all_tables;
  dbms_output.enable(4000);
  dbms_output.put_line(n);
end;
/


---- PostgreProcedure1 ----

create or replace function reffunc1() returns refcursor as $$
declare
    ref refcursor;
begin
    open ref for select * from buro.contact;
    return ref;
end;
$$ language plpgsql;







