/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 		GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.inquiry.iteminquiry;

import org.apache.log4j.Logger;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
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
public class MAXValidateItemLaunchShuttle implements ShuttleIfc
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

    protected MAXItemInquiryCargo cargo = null;

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
        this.cargo = (MAXItemInquiryCargo)bus.getCargo();
        
      //  System.out.println("70 :"+cargo.getEmpID());
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
        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo)bus.getCargo();
        
        // Set Store ID
        //cargo.setStoreID(this.cargo.getRegister().getWorkstation().getStoreID());
        cargo.setRegister(this.cargo.getRegister());
        
        // Set Flag for either going through the full Item Inquiry flow or 
        // just doing a PLU lookup
        cargo.setIsRequestForItemLookup(this.cargo.isRequestForItemLookup());
        //Change for Rev 1.0 :Starts
    //    cargo.setInterStateDelivery(this.cargo.getInterStateDelivery());
      //  cargo.setFromRegion(this.cargo.getFromRegion());
      //  cargo.setToRegion(this.cargo.getToRegion());
      //Change for Rev 1.0 : Ends
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
       // System.out.println("116 :"+cargo.getEmpID());
        cargo.setEmpID(this.cargo.getEmpID());
     // cargo.getPLUItem().getAdvancedPricingRules().get;
        //System.out.println("118 :"+this.cargo.getEmpID());
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
