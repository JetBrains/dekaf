---- create-table-sudoku ----
create table Sudoku
(
    Id integer not null
        constraint Sudoku_pk primary key,
    Rebus smallint[9][9],
    Solution smallint[9][9]
);

---- populate-table-sudoku ----
insert into Sudoku
values (1, e'{{0,0,0,2,6,0,7,0,1}, {6,8,0,0,7,0,0,9,0}, {1,9,0,0,0,4,5,0,0}}',
           e'{{4,3,5,2,6,9,7,8,1}, {6,8,2,5,7,1,4,9,3}, {1,9,7,8,3,4,5,6,2}}'),
       (2, e'{{0,0,0,2,6,0,7,0,1}, {6,8,0,0,7,0,0,9,0}, {1,9,0,0,0,4,5,0,0}}',
           e'{{4,3,5,2,6,9,7,8,1}, {6,8,2,5,7,1,4,9,3}, {1,9,7,8,3,4,5,6,2}}');

---- retrieve-one-sudoku ----
select Rebus
from Sudoku
where Rebus is not null
limit 1;

---- retrieve-couple-of-sudoku ----
select Rebus, Solution
from Sudoku
where Rebus is not null
limit 1;

---- retrieve-super-matrix ----
select Rebus, Solution
from Sudoku
order by Id;
