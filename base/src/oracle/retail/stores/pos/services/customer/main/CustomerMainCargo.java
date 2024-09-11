/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/main/CustomerMainCargo.java /main/14 2013/10/01 15:27:35 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.3  2004/02/12 16:49:33  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:56:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:32:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:13:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:50   msg
 * Initial revision.
 *
 *    Rev 1.1   16 Nov 2001 10:34:10   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:16:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.main;

// foundation imports
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

//--------------------------------------------------------------------------
/**
    Class that carries data among sites in the CustomerMain service.
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class CustomerMainCargo extends CustomerCargo
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6889979127673153019L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
        Flag indicating if this is the initial entry to Customer Service, for journalling
    **/
    protected boolean initialEntry = true;

    /**
        Flag indication if this is from a "Returns" transaction
    **/
    protected boolean isReturn = false;

    //---------------------------------------------------------------------
    /**
        Constructs CustomerMainCargo object. <P>
    **/
    //---------------------------------------------------------------------
    public CustomerMainCargo()
    {
        setLink(true);
    }

    //---------------------------------------------------------------------
    /**
        Returns whether this is the initial entry to Customer Service.
        If it is, then the text "Enter Customer" gets journalled. <P>
        @return true if this is the initial entry to Customer Service
    **/
    //---------------------------------------------------------------------
    public boolean isInitialEntry()
    {
        return initialEntry;
    }

    //---------------------------------------------------------------------
    /**
        Sets whether this is the initial entry to Customer Service.
        If it is, then the text "Enter Customer" gets journalled.
        Otherwise, not.<P>
        @param initialEntry true if this is initial entry to Cust Svc
    **/
    //---------------------------------------------------------------------
    public void setInitialEntry(boolean initialEntry)
    {
        this.initialEntry = initialEntry;
    }

    /**
     * Returns whether this is from a "Returns" transaction.
     * @return Whether this is from a "Returns" transaction.
     */
    public boolean isReturn()
    {
        return isReturn;
    }

    /**
     * Sets whether this is from a "Returns" transaction.
     * @param isReturn whether this is from a "Returns" transaction.
     */
    public void setReturn(boolean isReturn)
    {
        this.isReturn = isReturn;
    }

}
