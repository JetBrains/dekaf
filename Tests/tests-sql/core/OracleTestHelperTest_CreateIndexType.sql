---- INDEXTYPE ----

create type position_im authid current_user as object
(
  curnum  number,
  howmany number,
  lower_bound number,  --lower_bound and upper_bound are used for the
  upper_bound number,  --index-based functional implementation.
  static function odcigetinterfaces(ifclist out sys.odciobjectlist) return number,
  static function odciindexcreate
    (ia sys.odciindexinfo, parms varchar2, env sys.odcienv) return number,
  static function odciindextruncate (ia sys.odciindexinfo,
                                     env sys.odcienv) return number,
  static function odciindexdrop(ia sys.odciindexinfo,
                                env sys.odcienv) return number,
  static function odciindexinsert(ia sys.odciindexinfo, rid rowid,
                                  newval number, env sys.odcienv) return number,
  static function odciindexdelete(ia sys.odciindexinfo, rid rowid, oldval number,
                                  env sys.odcienv) return number,
  static function odciindexupdate(ia sys.odciindexinfo, rid rowid, oldval number,
                                  newval number, env sys.odcienv) return number,
  static function odciindexstart(sctx in out position_im, ia sys.odciindexinfo,
                                 op sys.odcipredinfo, qi sys.odciqueryinfo,
                                 strt number, stop number, lower_pos number,
                                 upper_pos number, env sys.odcienv) return number,
  member function odciindexfetch(self in out position_im, nrows number,
                                 rids out sys.odciridlist, env sys.odcienv)
                                 return number,
  member function odciindexclose(env sys.odcienv) return number
);
/




CREATE OR REPLACE TYPE BODY position_im
IS
   STATIC FUNCTION ODCIGETINTERFACES(ifclist OUT SYS.ODCIOBJECTLIST)
       RETURN NUMBER IS
BEGIN
    ifclist := SYS.ODCIOBJECTLIST(SYS.ODCIOBJECT('SYS','ODCIINDEX2'));
    RETURN ODCICONST.SUCCESS;
END ODCIGETINTERFACES;

STATIC FUNCTION ODCIINDEXCREATE (ia SYS.ODCIINDEXINFO, parms VARCHAR2, env SYS.ODCIEnv) RETURN NUMBER
IS
stmt   VARCHAR2(2000);
BEGIN
-- construct the sql statement
stmt := 'Create Table ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
        '_STORAGE_TAB' || '(col_val, base_rowid, PRIMARY KEY ' ||
        '(col_val, base_rowid)) ORGANIZATION INDEX AS SELECT ' ||
        ia.INDEXCOLS(1).COLNAME || ', ROWID FROM ' ||
        ia.INDEXCOLS(1).TABLESCHEMA || '.' || ia.INDEXCOLS(1).TABLENAME;
dbms_output.put_line(stmt);
EXECUTE IMMEDIATE stmt;
RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXDROP(ia SYS.ODCIINDEXINFO, env SYS.ODCIEnv) RETURN NUMBER IS
stmt VARCHAR2(2000);
BEGIN
 -- construct the sql statement
stmt := 'DROP TABLE ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
'_STORAGE_TAB';

EXECUTE IMMEDIATE stmt;
RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXTRUNCATE(ia SYS.ODCIINDEXINFO, env SYS.ODCIEnv) RETURN NUMBER IS
stmt VARCHAR2(2000);
BEGIN
-- construct the sql statement
stmt := 'TRUNCATE TABLE ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME || '_STORAGE_TAB';

EXECUTE IMMEDIATE stmt;
RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXINSERT(ia SYS.ODCIINDEXINFO, rid ROWID,
                       newval NUMBER, env SYS.ODCIEnv) RETURN NUMBER IS
stmt VARCHAR2(2000);
BEGIN
 -- construct the sql statement
stmt := 'INSERT INTO ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
       '_STORAGE_TAB  VALUES (''' || newval || ''' , ''' || rid || ''' )';

-- execute the statement
EXECUTE IMMEDIATE stmt;

RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXDELETE(ia SYS.ODCIINDEXINFO, rid ROWID, oldval NUMBER,
                               env SYS.ODCIEnv)
  RETURN NUMBER IS
stmt VARCHAR2(2000);
BEGIN
 -- construct the sql statement
stmt := 'DELETE FROM ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
       '_STORAGE_TAB WHERE col_val = ''' || oldval || ''' AND base_rowid = ''' || rid || '''';

-- execute the statement
EXECUTE IMMEDIATE stmt;

RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXUPDATE(ia SYS.ODCIINDEXINFO, rid ROWID, oldval NUMBER,
                       newval NUMBER, env SYS.ODCIEnv) RETURN NUMBER IS
stmt VARCHAR2(2000);
BEGIN
 -- construct the sql statement
stmt := 'UPDATE ' || ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
       '_STORAGE_TAB SET col_val = ''' || newval || ''' WHERE f2 = '''|| rid ||'''';

-- execute the statement
EXECUTE IMMEDIATE stmt;

RETURN ODCICONST.SUCCESS;
END;

STATIC FUNCTION ODCIINDEXSTART(SCTX IN OUT position_im, ia SYS.ODCIINDEXINFO,
                      op SYS.ODCIPREDINFO, qi SYS.ODCIQUERYINFO,
                      strt NUMBER, stop NUMBER, lower_pos NUMBER,
                      upper_pos NUMBER, env SYS.ODCIEnv) RETURN NUMBER IS

 rid              VARCHAR2(5072);
 storage_tab_name VARCHAR2(65);
 lower_bound_stmt VARCHAR2(2000);
 upper_bound_stmt VARCHAR2(2000);
 range_query_stmt VARCHAR2(2000);
 lower_bound      NUMBER;
 upper_bound      NUMBER;
 cnum             INTEGER;
 nrows            INTEGER;

BEGIN
 -- Take care of some error cases.
 -- The only predicates in which position operator can appear are
 --    op() = 1     OR
 --    op() = 0     OR
 --    op() between 0 and 1
 IF (((strt != 1) AND (strt != 0)) OR
     ((stop != 1) AND (stop != 0)) OR
     ((strt = 1) AND (stop = 0))) THEN
   RAISE_APPLICATION_ERROR(-20101,
                           'incorrect predicate for position_between operator');
 END IF;

 IF (lower_pos > upper_pos) THEN
   RAISE_APPLICATION_ERROR(-20101, 'Upper Position must be greater than or
   equal to Lower Position');
 END IF;

 IF (lower_pos <= 0) THEN
   RAISE_APPLICATION_ERROR(-20101, 'Both Positions must be greater than zero');
 END IF;

 storage_tab_name := ia.INDEXSCHEMA || '.' || ia.INDEXNAME ||
                     '_STORAGE_TAB';
 upper_bound_stmt := 'Select MIN(col_val) FROM (Select /*+ INDEX_DESC(' ||
                     storage_tab_name || ') */ DISTINCT ' ||
                     'col_val FROM ' || storage_tab_name || ' ORDER BY ' ||
                     'col_val DESC) WHERE rownum <= ' || lower_pos;
 EXECUTE IMMEDIATE upper_bound_stmt INTO upper_bound;

 IF (lower_pos != upper_pos) THEN
   lower_bound_stmt := 'Select MIN(col_val) FROM (Select /*+ INDEX_DESC(' ||
                       storage_tab_name || ') */ DISTINCT ' ||
                       'col_val FROM ' || storage_tab_name ||
                       ' WHERE col_val < ' || upper_bound || ' ORDER BY ' ||
                       'col_val DESC) WHERE rownum <= ' ||
                       (upper_pos - lower_pos);
   EXECUTE IMMEDIATE lower_bound_stmt INTO lower_bound;
 ELSE
   lower_bound := upper_bound;
 END IF;

 IF (lower_bound IS NULL) THEN
   lower_bound := upper_bound;
 END IF;

 range_query_stmt := 'Select base_rowid FROM ' || storage_tab_name ||
                     ' WHERE col_val BETWEEN ' || lower_bound || ' AND ' ||
                     upper_bound;

 cnum := DBMS_SQL.OPEN_CURSOR;
 DBMS_SQL.PARSE(cnum, range_query_stmt, DBMS_SQL.NATIVE);

 -- set context as the cursor number
 SCTX := position_im(cnum, 0, 0, 0);

 -- return success
 RETURN ODCICONST.SUCCESS;
END;

MEMBER FUNCTION ODCIINDEXFETCH(SELF IN OUT position_im, nrows NUMBER,
                              rids OUT SYS.ODCIRIDLIST, env SYS.ODCIEnv)
RETURN NUMBER IS
 cnum    INTEGER;
 rid_tab DBMS_SQL.Varchar2_table;
 rlist   SYS.ODCIRIDLIST := SYS.ODCIRIDLIST();
 i       INTEGER;
 d       INTEGER;
BEGIN
 cnum := SELF.curnum;

 IF self.howmany = 0 THEN
   dbms_sql.define_array(cnum, 1, rid_tab, nrows, 1);
   d := DBMS_SQL.EXECUTE(cnum);
 END IF;

 d := DBMS_SQL.FETCH_ROWS(cnum);

 IF d = nrows THEN
   rlist.extend(d);
 ELSE
   rlist.extend(d+1);
 END IF;

 DBMS_SQL.COLUMN_VALUE(cnum, 1, rid_tab);

 for i in 1..d loop
   rlist(i) := rid_tab(i+SELF.howmany);
 end loop;

 SELF.howmany := SELF.howmany + d;
 rids := rlist;

 RETURN ODCICONST.SUCCESS;
END;

MEMBER FUNCTION ODCIINDEXCLOSE(env SYS.ODCIEnv) RETURN NUMBER IS
 cnum INTEGER;
BEGIN
 cnum := SELF.curnum;
 DBMS_SQL.CLOSE_CURSOR(cnum);
 RETURN ODCICONST.SUCCESS;
END;

END;
/



CREATE OR REPLACE FUNCTION function_for_position_between
                           (col NUMBER, lower_pos NUMBER, upper_pos NUMBER,
                            indexctx IN SYS.ODCIIndexCtx,
                            scanctx IN OUT position_im,
                            scanflg IN NUMBER)
RETURN NUMBER AS
  rid              ROWID;
  storage_tab_name VARCHAR2(65);
  lower_bound_stmt VARCHAR2(2000);
  upper_bound_stmt VARCHAR2(2000);
  col_val_stmt     VARCHAR2(2000);
  lower_bound      NUMBER;
  upper_bound      NUMBER;
  column_value     NUMBER;
BEGIN
IF (indexctx.IndexInfo IS NOT NULL) THEN
 storage_tab_name := indexctx.IndexInfo.INDEXSCHEMA || '.' ||
                     indexctx.IndexInfo.INDEXNAME || '_STORAGE_TAB';
 IF (scanctx IS NULL) THEN
   --This is the first call.  Open a cursor for future calls.
   --First, do some error checking
   IF (lower_pos > upper_pos) THEN
     RAISE_APPLICATION_ERROR(-20101,
       'Upper Position must be greater than or equal to Lower Position');
   END IF;
   IF (lower_pos <= 0) THEN
     RAISE_APPLICATION_ERROR(-20101,
       'Both Positions must be greater than zero');
   END IF;
   --Obtain the upper and lower value bounds for the range we're interested
   --in.
   upper_bound_stmt := 'Select MIN(col_val) FROM (Select /*+ INDEX_DESC(' ||
                     storage_tab_name || ') */ DISTINCT ' ||
                     'col_val FROM ' || storage_tab_name || ' ORDER BY ' ||
                     'col_val DESC) WHERE rownum <= ' || lower_pos;
   EXECUTE IMMEDIATE upper_bound_stmt INTO upper_bound;
   IF (lower_pos != upper_pos) THEN
     lower_bound_stmt := 'Select MIN(col_val) FROM (Select /*+ INDEX_DESC(' ||
                         storage_tab_name || ') */ DISTINCT ' ||
                         'col_val FROM ' || storage_tab_name ||
                         ' WHERE col_val < ' || upper_bound || ' ORDER BY ' ||
                         'col_val DESC) WHERE rownum <= ' ||
                         (upper_pos - lower_pos);
     EXECUTE IMMEDIATE lower_bound_stmt INTO lower_bound;
   ELSE
     lower_bound := upper_bound;
   END IF;
   IF (lower_bound IS NULL) THEN
     lower_bound := upper_bound;
   END IF;
   --Store the lower and upper bounds for future function invocations for
   --the positions.
   scanctx := position_im(0, 0, lower_bound, upper_bound);
 END IF;
 --Fetch the column value corresponding to the rowid, and see if it falls
 --within the determined range.
 col_val_stmt := 'Select col_val FROM ' || storage_tab_name ||
                 ' WHERE base_rowid = ''' || indexctx.Rid || '''';
 EXECUTE IMMEDIATE col_val_stmt INTO column_value;
 IF (column_value <= scanctx.upper_bound AND
     column_value >= scanctx.lower_bound AND
     scanflg = ODCICONST.RegularCall) THEN
   RETURN 1;
 ELSE
   RETURN 0;
 END IF;
ELSE
 RAISE_APPLICATION_ERROR(-20101, 'A column that has a domain index of' ||
                         'Position indextype must be the first argument');
END IF;
END;
/




create or replace operator position_between
   binding (number, number, number) return number
   with index context, scan context position_im
   using function_for_position_between
/



create indextype position_indextype
   for position_between(number, number, number)
   using position_im
/



create table Employee
(
    Name varchar(26) primary key,
    Salary number(6)
)
/


create index Employee_SI
    on Employee (Salary)
    indextype is position_indextype
/

