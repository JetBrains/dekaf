Dekaf
=====

Java Database Access Layer Framework

Dekaf is Java framework for working with databases, primarily via JDBC. Dekaf is NOT an ORM.


NOW THIS FRAMEWORK IS UNDER DEVELOPMENT.

The currently developed version: 2.0.0.X (where X is a build number).


Motivation
----------

Three categories of problems lead me to start authoring a new DB layer framework.
 
JDBC problems:

* too verbose code one have to write for every simple call to a DB procedure and for every simple query
* too many code for data passing and converting
* the damn SQLException that is the same for all kinds of errors coming from DBMS, without possibility to handle same exceptions from different RDBMS by the same way
* JDBC doesn't support named parameters

DB dialect problems:

* different RDBMS have different SQL dialects, and when one wants to support several RDBMS they have to write every query several times (one for every SQL dialect), or to construct queries dynamically (that makes code unreadable)
* different RDBMS reports errors differently that requires different error handling for every query
* different RDBMS requires different data converting, that makes exception handling code overcomplicated
 
Development and debug of applications problem
 
* due to the problems mentioned above, one have to construct their queries dynamically, that obstructs to try and debug these queries right from the code  



Planned Features
----------------

Key features to address the problems mentioned above

* SQL queries are in separated .sql files, that allow to try/debug them right from these files
* SQL macros that allow to unify queries and reduce dynamic query text constructions
* named parameter
* typed out parameters 
* retrieve data as commonly used Java structures (arrays, lists, sets)
* unified exceptions hierarchy

Supported RDBMS:

* PostgreSQL     9+
* Oracle         9+
* MS SQL Server  9+
* MySQL          5.1+
* H2             1.4+

Planned supported RDBMS:

* Azure
* RedShift
* SQLite


The Name
--------

The name 'Dekaf' means that this framework allows to
avoid all pains and problems belonging naturally to pure JDBC API.

A coffee (Java) without headache.

P.S. The former name of this framework is JDBA (just Java DataBase Access).
