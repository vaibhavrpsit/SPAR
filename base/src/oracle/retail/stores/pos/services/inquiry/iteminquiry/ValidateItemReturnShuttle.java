/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ValidateItemReturnShuttle.java /main/11 2012/10/11 14:08:29 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      10/11/12 - enable WebStore search flow.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   13 Nov 2003 10:44:14   jriggins
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// java imports
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modifyItem service to the cargo used in the Alterations service. <P>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class ValidateItemReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8723238179941890264L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.inquiry.iteminquiry.ValidateItemReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //TODO protected ValidateItemCargoIfc cargo = null;
    protected ItemInquiryCargo cargo = null;

    //----------------------------------------------------------------------
    /**
       Loads cargo from itemvalidate service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Search results have been placed in the ValidateItemCargo instance
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>Shuttle has a reference to the ValidateItemCargo instance in its Bus
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        //TODO this.cargo = (ValidateItemCargoIfc)bus.getCargo();
        this.cargo = (ItemInquiryCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Loads data into validate item service. <P>
       <B>Pre-Condition(s)</B>
       <UL>Search results have been placed in the ValidateItemCargo instance to which this Shuttle
       has a reference
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>ItemInquiryCargo will contain the search results of a particular inquiry
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        PLUItemIfc itemList[] = this.cargo.getItemList();
        PLUItemIfc item = this.cargo.getPLUItem();
        
        cargo.setItemList(itemList);
        cargo.setPLUItem(item);
        cargo.setItemFromWebStore(this.cargo.isItemFromWebStore());
        
        cargo.setDataExceptionErrorCode(this.cargo.getDataExceptionErrorCode());
                
        Vector invalidFields = this.cargo.getInvalidFields();
        if (invalidFields != null)            
        {
            for (int x = 0; x < invalidFields.size(); x++)
            {
                int invalidField = ((Integer)invalidFields.get(x)).intValue();
                cargo.setInvalidField(invalidField);                
            }
        }        
        
        cargo.setModifiedFlag(itemList != null || item != null);        
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
