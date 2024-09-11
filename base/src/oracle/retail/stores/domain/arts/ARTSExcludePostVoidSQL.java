/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSExcludePostVoidSQL.java /main/15 2012/05/21 15:50:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                      rgbustores_13.5x_generic
 *    cgreen 05/16/12 - arrange order of businessDay column to end of primary
 *                      key to improve performance since most receipt lookups
 *                      are done without the businessDay
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:10:49 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     12/14/2005 13:00:14    Deepanshu       CR
           4896: Exclude PostVoid transactions from retreived transactions
      3    360Commerce1.2         3/31/2005 15:27:14     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:40     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:31     Robert Pearse
     $
     Revision 1.4  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Sep 17 2003 17:32:52   ixb1
 * Modified method buildSQL() to build SQL AND NOT statement instead of sub-select - to support mysql.
 *
 *    Rev 1.1   Sep 03 2003 16:21:36   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 *
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:34:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:44:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:06   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;

/**
 * Creates sub-select SQL to exclude voided transactions from transaction
 * retrievals.
 * 
 * @version $Revision: /main/15 $
 */
public class ARTSExcludePostVoidSQL implements Serializable, ARTSDatabaseIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1920939852511577954L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Builds SQL AND NOT statement instead of sub-select to eliminate
     * post-voided transactions from transaction retrievals.
     * 
     * @param sql SQLSelectStatement
     */
    public static void buildSQL(SQLSelectStatement sql) throws SQLException
    {
        sql.addTable(TABLE_POST_VOID_TRANSACTION, ALIAS_POST_VOID_TRANSACTION);
        sql.setDistinctFlag(true);
        sql.addNotQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = "
                + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addNotQualifier(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = "
                + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addNotQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_VOIDED);
        sql.addNotQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
    }

    /**
     * Builds SQL NOT IN statement instead of sub-select to eliminate
     * post-voided transactions from transaction retrievals.
     * 
     * @param sql SQLSelectStatement
     * @param voidTransaction Vector
     */
    public static void buildSQL(SQLSelectStatement sql, Vector<Integer> voidTransaction) throws SQLException
    {
        // exclude post void transaction
        for (Enumeration<Integer> e = voidTransaction.elements(); e.hasMoreElements();)
        {
            sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " not in ("
                    + e.nextElement() + ")");
        }
    }

}
