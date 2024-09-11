/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadCustomerAction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:33:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:42:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:50:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:56:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;

import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    The DataAction to insert a new Customer into the database. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class ReadCustomerAction
    implements DataActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7908878596705662475L;

    /** The owning transaction **/
    protected DataTransactionIfc parent = null;

    /** The data Object **/
    protected Serializable dataObject = null;

    protected static final String OPERATION_NAME = "ReadCustomer";

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
        @param  transaction The DataTransaction that this action belongs to
    **/
    //---------------------------------------------------------------------
    public ReadCustomerAction(DataTransactionIfc transaction,
                              ARTSCustomer customer)
    {
        parent = transaction;
        dataObject = customer;
    }

    //---------------------------------------------------------------------
    /**
        @return  the data Object
    **/
    //---------------------------------------------------------------------
    public Serializable getDataObject()
    {
        return dataObject;
    }

    //---------------------------------------------------------------------
    /**
        @return  The operation name
    **/
    //---------------------------------------------------------------------
    public String getDataOperationName()
    {
        return OPERATION_NAME;
    }

}
