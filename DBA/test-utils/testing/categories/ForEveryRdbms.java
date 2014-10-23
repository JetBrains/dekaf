package testing.categories;

/**
 * Tests that should be performed for every RDBMS.
 */
public interface ForEveryRdbms extends ForPostgre, ForOracle, ForMSSql, ForMySql
{}
