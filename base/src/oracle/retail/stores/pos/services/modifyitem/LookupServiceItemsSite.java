/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/LookupServiceItemsSite.java /main/18 2012/12/21 12:49:00 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    sgu    12/20/12 - use locale requestor
 *    jswan  09/25/12 - Modified to support retrieval of the list of Service
 *                      (non-merchandise) items.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sgu    12/08/09 - rework PLURequestor to use EnumSet and rename
 *                      set/unsetRequestType to add/removeRequestType
 *    sgu    11/30/09 - add plu requestor to return plu information selectively
 *    ddbake 10/22/08 - Updating to use localized item descriptoins
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/15/08 - I18Ning manufacturer name
 *
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
     $
     Revision 1.7  2004/06/30 15:12:48  jdeleau
     @scr 5868 Get tax rules with service item PLUs.

     Revision 1.6  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.5  2004/04/17 17:59:28  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:06  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:03  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:39:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:01:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Mar 31 2003 16:09:08   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.2   Mar 05 2003 10:44:38   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Oct 01 2002 10:20:10   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:17:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:18   msg
 * Initial revision.
 *
 *    Rev 1.2   22 Feb 2002 14:02:10   baa
 * remove duplicate items from servicelist
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Feb 05 2002 16:42:40   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:29:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:10   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifyitem;
// java imports
import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
    This site queries the database for all service items.
    It mails a Success letter if the item is found.
    It mails a Failure letter if the item is not found.
    @version $Revision: /main/18 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class LookupServiceItemsSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    //----------------------------------------------------------------------
    /**
       Queries the database for all service items.
       A Success letter is mailed if at least one item
       is found. A Failure letter is mailed if no item found.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Grab the item number from the cargo
        ItemCargo cargo = (ItemCargo)bus.getCargo();

        AdvItemSearchResults serviceItems = null;
        String letterName = null;
        try
        {
            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setLocaleRequestor(new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
            PLURequestor pluRequestor = new PLURequestor(false);
            inquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
            inquiry.setPLURequestor(pluRequestor);
            inquiry.setItemTypeCode(ARTSDatabaseIfc.ITEM_TYPE_SERVICE);

            PLUTransaction pluTransaction = (PLUTransaction) 
                DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);
            serviceItems = pluTransaction.getItemsForAdvancedSearch(inquiry);

            cargo.setServiceItems(serviceItems);
            letterName = CommonLetterIfc.SUCCESS;
        }
        catch (DataException de)
        {
            logger.warn( "No service items found: " + de.getMessage() + "");
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            letterName = CommonLetterIfc.FAILURE;
        }

        // Proceed to next site
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  LookupServiceItemsSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
