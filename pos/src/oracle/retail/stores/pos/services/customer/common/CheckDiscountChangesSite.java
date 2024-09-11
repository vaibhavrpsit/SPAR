/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CheckDiscountChangesSite.java /main/13 2013/11/08 16:30:12 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  11/08/13 - customer group change not persisted when tour
 *                         reloads page
 *    rabhawsa  06/17/13 - reset the original customer groups in case override
 *                         has failed.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/12/10 - Added code to reset the Customer Discount when the
 *                         user doesnt have Access for the setting the customer
 *                         discount.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/16 22:30:42  jdeleau
 *   @scr 5621 Fix not saving the customer discount group for businesses
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 27 2003 08:48:00   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Mar 20 2003 20:44:30   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import max.retail.stores.domain.customer.MAXCustomer;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    Checks for changes on the customer discount information
**/
//--------------------------------------------------------------------------
public class CheckDiscountChangesSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";



    //----------------------------------------------------------------------
    /**
     * Checks for changes on the customer discount information
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;
        
         
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc originalCustomer = cargo.getOriginalCustomer();
        CustomerIfc customer = cargo.getCustomer();
        MAXCustomer cust=null;
        // below changes made by aks for lmr e wallet flag
        boolean iswalletFlag=false;
        if(cargo.getCustomer() instanceof MAXCustomer)
        {
        	iswalletFlag=((MAXCustomer)customer).isLMREWalletCustomerFlag();
        }
        if(originalCustomer instanceof MAXCustomer)
        {
        	((MAXCustomer)originalCustomer).setLMREWalletCustomerFlag(iswalletFlag);
        }
     // Above changes made by aks for lmr e wallet flag...changes end here
        customer.setCustomerGroups(originalCustomer.getCustomerGroups());
        
       // handle possible change in customer group
        CustomerGroupIfc newCustomerGroup = null;
        String oldCustomerGroupID = null;
        CustomerGroupIfc[] groups = customer.getCustomerGroups();
        // get old customer group ID
        if (groups != null &&
            groups.length > 0)
        {
            oldCustomerGroupID = groups[0].getGroupID();
        }

        // get index of customer group selection from bean model
        int index = cargo.getSelectedCustomerGroup();
        // index zero is (none) and leaves newCustomerGroup at null
        if (index > 0)
        {
            int numberGroups = cargo.getNumberCustomerGroups();
            if (numberGroups > 0 &&
                index < numberGroups)
            {
                newCustomerGroup = cargo.getCustomerGroups()[index];
            }
        }

        cargo.setAccessFunctionID(RoleFunctionIfc.PREFERRED_CUSTOMER);
       // pull off new customer group ID for comparison
        String newCustomerGroupID = null;
        if (newCustomerGroup != null)
        {
            newCustomerGroupID = newCustomerGroup.getGroupID();
        }
        // first case, for any non null value of customer discount authorization should be done
        // another case, while changing the discount type from not null to null during find  operation
        // price override check is needed in the scenario when access for the user
        // has already been checked for manager override and permission is granted
        
        if (
            (
               !Util.isObjectEqual(newCustomerGroupID, oldCustomerGroupID) ||
               cargo.getDiscountTypeChanged()
            ) 
            &&
            (
               (newCustomerGroup != null && !(cargo.getOverride())) ||
               (newCustomerGroup == null && !(cargo.getOverride()) && cargo.getAddFind())
             )
           )
        {
            letterName = "CheckAccess";
        }

        
        // if a change in group ID's, check access and implement as needed

        if (!(Util.isObjectEqual(newCustomerGroupID, oldCustomerGroupID)))
        {
            if (newCustomerGroup != null)
     	    {
     		   cargo.getCustomer().setCustomerGroups(
                     new CustomerGroupIfc[] {newCustomerGroup} );

     	    }

           // if the discount type is changed from not null to null value
           // the previous added not null group has to be deleted else the
           // customer group gets saved in the db with not null group details
     	    if (oldCustomerGroupID != null  && newCustomerGroup == null)
     	    {
     		   cargo.getCustomer().deleteCustomerGroups(new CustomerGroupIfc[] {groups[0]} );
     	    }
     	    cargo.setDiscountTypeChanged(true);
            cargo.setSelectedCustomerGroup(index);


        }
        else
        {
            // if user is not authorized and the discount type is not changed
            // then user
            // should be allowed to proceed further without prompting for
            // manager override
            if (!cargo.getDiscountTypeChanged())
            {
                letterName = CommonLetterIfc.CONTINUE;
            }
        }


        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }


}
