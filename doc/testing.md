Testing
=======
JUnit 5 is used for unit- and integration testing (minimal version is 5.1).

Used categorization tags:

* **basic** — normal unit tests that don't require connection to real database servers. The can use H2 in memory databases.
* **demo** — demonstration tests or tests that call demo blocks.
* **jdbc** — tests on how Dekaf locates and loads JDBC drivers.
* **connection** — tests on how Dekaf connects to real RDBMS servers and handles connmection problems. 
* **int** — integration tests.
* **fail** — tests that fail in order to show fail messages (such tests are not included into regression testing suits).

