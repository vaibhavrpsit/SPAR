/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/InquiryOptionsLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:51:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jan 12 2003 16:03:54   pjf
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap(), setCodeListMap().
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.1   Oct 14 2002 16:10:06   DCobb
 * Added alterations service to item inquiry service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * 
 *    Rev 1.0   Apr 29 2002 15:16:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 17:21:46   baa
 * get department list from reason codes
 * Resolution for POS SCR-1562: Get Department list from Reason Codes, not separate Dept. list.
 *
 *    Rev 1.0   Mar 18 2002 11:37:02   msg
 * Initial revision.
 *
 *    Rev 1.2   Dec 11 2001 20:51:52   dfh
 * pass along transaction type
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.1   25 Oct 2001 17:43:10   baa
 *
 * cross store inventory feature
 *
 * Resolution for POS SCR-230: Cross Store Inventory
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// Java imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.InquiryOptionsCargo;

//--------------------------------------------------------------------------
/**
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class InquiryOptionsLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -82268067153917575L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // Calling service's cargo
    protected ItemCargo itemCargo = null;

    //----------------------------------------------------------------------
    /**
        Loads the item cargo.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);

        itemCargo = (ItemCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Transfers the item cargo to the item inquiry cargo for the item inquiry service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);

        InquiryOptionsCargo inquiryCargo = (InquiryOptionsCargo) bus.getCargo();
        inquiryCargo.setRegister(itemCargo.getRegister());
        inquiryCargo.setTransactionType(itemCargo.getTransactionType());
        inquiryCargo.setTransaction(itemCargo.getTransaction());
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                         
        return "Class:  InquiryOptionsLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode();
    }                                

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }                               
}
