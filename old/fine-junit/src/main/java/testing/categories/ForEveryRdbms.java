package testing.categories;

/**
 * Tests that should be performed for every RDBMS.
 */
public interface ForEveryRdbms extends ForPostgres, ForOracle, ForMSSQL, ForMySQL
{}
