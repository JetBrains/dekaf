package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.util.Version;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgresTestHelper extends BaseTestHelper<DBFacade> {

  public PostgresTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(PostgresTestHelper.class));
    schemasNotToZap.add("pg_catalog");
  }


  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    performCommand(scriptum, "X1000000");
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    ConnectionInfo info = db.getConnectionInfo();
    Version version = info.serverVersion.truncateNegatives();
    if (version.isOrGreater(9, 1)) {
      performMetaQueryCommands(scriptum, "ZapExtensionsMetaQuery");
      performMetaQueryCommands(scriptum, "ZapCollationsMetaQuery");
    }

    String metaQueryName = version.isOrGreater(11) ? "ZapSchemaMetaQuery11" : "ZapSchemaMetaQuery";
    performMetaQueryCommands(scriptum, metaQueryName);
  }
}
