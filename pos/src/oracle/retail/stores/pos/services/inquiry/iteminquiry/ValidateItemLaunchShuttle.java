/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ValidateItemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/10/13 08:59:53 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  09/11/14 - pass isRetrieveFromStore to enable webstore lookup
 *    tksharma  10/11/11 - skipUOMCheck introduced in cargo
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:42 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/23 16:16:01  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.5  2004/06/01 17:55:30  mkp1
 *   @scr 2775 Fixed PLU to return correct tax calculator
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   13 Nov 2003 10:44:12   jriggins
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import org.apache.log4j.Logger;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

// pos imports


//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modifyItem service to the cargo used in the Alterations service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ValidateItemLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5777907852826638044L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.inquiry.iteminquiry.ValidateItemLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected ItemInquiryCargo cargo = null;

    //----------------------------------------------------------------------
    /**
       Loads cargo from iteminquiry service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo has set the search criteria for a particular item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>Shuttle has a reference to the search criteria for a particular item
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------

    public void load(BusIfc bus)
    {
        this.cargo = (ItemInquiryCargo)bus.getCargo();
        MAXPLUItemIfc plu  = (MAXPLUItemIfc)bus.getCargo();
        
    }

    //----------------------------------------------------------------------
    /**
       Loads data into itemvalidate service. <P>
       <B>Pre-Condition(s)</B>
       <UL>Calling service has set the search criteria for a particular item
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the search criteria for a particular item
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        //TODO ValidateItemCargoIfc cargo = (ValidateItemCargoIfc)bus.getCargo();
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        
        // Set Store ID
        //cargo.setStoreID(this.cargo.getRegister().getWorkstation().getStoreID());
        cargo.setRegister(this.cargo.getRegister());
        
        // Set Flag for either going through the full Item Inquiry flow or 
        // just doing a PLU lookup
        cargo.setIsRequestForItemLookup(this.cargo.isRequestForItemLookup());
        
        // Set Search Criteria
        SearchCriteriaIfc criteria = this.cargo.getInquiry();        
        cargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                         criteria.getItemNumber(), 
                         criteria.getDescription(),
                         criteria.getDepartmentID(),
                         criteria.getGeoCode()); 
        SearchCriteriaIfc newCriteria = cargo.getInquiry();
        newCriteria.setRetrieveFromStore(criteria.isRetrieveFromStore());
        cargo.setInquiry(newCriteria);
        
        // set whether or not it is an related item lookup
        cargo.setRelatedItem(this.cargo.isRelatedItem());
        cargo.skipUOMCheck(this.cargo.isSkipUOMCheck());
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
