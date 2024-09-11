/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/demographic/ValidateCustomerInfoAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/09 18:21:10  jdeleau
 *   @scr 6074 Customer Phone# was cutting off last digit.
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Nov 13 2003 11:10:22   baa
 * sale refactoring
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.1   Nov 07 2003 07:45:18   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 13:58:48   jriggins
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.demographic;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ValidateCustomerInfoAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5568259080459769669L;

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String LANENAME = "ValidateCustomerInfoAisle";

    //--------------------------------------------------------------------------
    /**     Gets the store number from UI
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = ( POSUIManagerIfc )bus.getManager( UIManagerIfc.TYPE );
        SaleCargoIfc cargo = ( SaleCargoIfc )bus.getCargo();

        // initialize models
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.PROMPT_CUSTOMER_INFO);
        PromptAndResponseModel prModel= model.getPromptAndResponseModel();

        // get customer info from cargo
        CustomerInfoIfc customerInfo = cargo.getCustomerInfo();

        // determine type of customer info
        int customerInfoType = customerInfo.getCustomerInfoType();

        if(customerInfoType != CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE)
        {
            // get user input
            String value = prModel.getResponseText();

            // postal code entered
            if(customerInfoType == CustomerInfoIfc.CUSTOMER_INFO_TYPE_POSTAL_CODE)
            {
                customerInfo.setPostalCode(value);
            }
            else
            // phone number entered
            if(customerInfoType == CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER)
            {
                 PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
                 phone.setPhoneNumber(value);
                 customerInfo.setPhoneNumber(phone);
             }
         }

         // save customer info to cargo
         cargo.setCustomerInfo(customerInfo);
         bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
