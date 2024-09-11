/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/CheckItemTypeReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/08/05 22:17:54  dcobb
 *   @scr 6655 Remove letter checks from shuttles.
 *   Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Jan 09 2004 12:54:28   lzhao
 * set transaction back, remove comments, add date
 * Resolution for 3666: Eltronic Journal for Gift Card Issue  and Reload not Correct
 * 
 *    Rev 1.2   04 Dec 2003 16:41:42   Tim Fritz
 * Fixed the problem where an Alteration SKU (40010001) was causing the POS app to crash.  Resolution for SCR 3530
 * 
 *    Rev 1.1   Nov 17 2003 08:41:08   jriggins
 * Setting appropriate ItemInquiryCargo attributes in unload()
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   13 Nov 2003 10:44:08   jriggins
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// java imports

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modifyItem service to the cargo used in the Alterations service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckItemTypeReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8662549187504574416L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.inquiry.iteminquiry.CheckItemTypeReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected ItemInquiryCargo itemInquiryCargo = null;

    //----------------------------------------------------------------------
    /**
       Loads cargo from itemvalidate service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo in the itemcheck service's bus has been modified as 
       appropriate for the type of item it maintains.
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>Shuttle has a reference to the ItemInquiryCargo instance in the itemcheck service
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        
        itemInquiryCargo = (ItemInquiryCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Loads data into validate item service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo in the itemcheck service's bus has been modified as 
       appropriate for the type of item it maintains.
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo instance of the calling service will be modified to reflect the 
       changes made by the itemcheck service
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        
        cargo.setPLUItem(itemInquiryCargo.getPLUItem());
        cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
        cargo.setTransaction(itemInquiryCargo.getTransaction());
        cargo.setModifiedFlag(itemInquiryCargo.getModifiedFlag());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ValidateItemLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class TenderLaunchShuttle
