create sequence Topic_seq
    order nocache
/

create table Topic (
    Id number(9) not null primary key,
    Name varchar(26) not null unique,
    Note nvarchar2(160),
    Created_Timestamp date default sysdate not null,
    Modified_Timestamp date,
    Modified_Times number(6) default 1 not null
)
/

create trigger Topic_tbi
    before insert on Topic
    for each row
begin
    if (:new.Id is null)
        then
            select Topic_seq.nextval
                into :new.Id
                from dual;
        end if;
end;
/

create trigger Topic_tbu
    before update on Topic
    for each row
begin
    :new.Modified_Times = :old.Modified_Times + 1;
end;
/

insert into Topic (Name, Note)
       values ('Topic1', 'The first topic')
/
insert into Topic (Name, Note)
       values ('Topic2', 'The second topic')
/

commit
/
