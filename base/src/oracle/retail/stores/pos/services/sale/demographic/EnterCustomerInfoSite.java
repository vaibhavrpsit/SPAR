/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/demographic/EnterCustomerInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/27/2006 4:27:36 PM   Brett J. Larsen 6131:
 *          Updated text for ZipCode/Telephone No for request/response panel
 *
 *         v7x -> 360Commerce merge
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *
 *
 *    4    .v7x      1.2.1.0     4/28/2006 7:24:54 AM   Dinesh Gautam   CR
 *         6131: Updated text for ZipCode/Telephone No for request/response
 *         panel
 *
 *
 *
 *   Revision 1.7  2004/07/27 22:29:28  jdeleau
 *   @scr 6485 Make sure the undo button on the sell item screen does
 *   not force the operator to re-enter the users zip of phone.
 *
 *   Revision 1.6  2004/07/22 00:06:35  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.5  2004/07/12 15:52:59  aachinfiev
 *   @scr 6075, 6076 - Added spaces to PhoneNumber & ZipCode parameters for display
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
 *
 *    Rev 1.2   08 Nov 2003 01:22:44   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.1   Nov 07 2003 07:45:20   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 05 2003 13:58:38   jriggins
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
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
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ado.utility.*;
import oracle.retail.stores.pos.ado.ADOException;

//------------------------------------------------------------------------------
/**
    Gets journal data and displays it.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EnterCustomerInfoSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "EnterCustomerInfoSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       domain properties
    **/
    public static final String DOMAIN_PROPERTIES = "domain.properties";

    //--------------------------------------------------------------------------
    /**
       Arrive at site, set up data vector and do search OR
       process a letter since we've been here before
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String parameterValue;
        

        parameterValue = "None";
        // get PromptForCustomerInfo Parameter
        try
        {
            parameterValue = pm.getStringValue("PromptForCustomerInformation");
        }
        catch (ParameterException pe)
        {
            logger.error( "" + pe.getMessage() + "");
        }

        // instantiate customer info object
        CustomerInfoIfc customerInfo = DomainGateway.getFactory().getCustomerInfoInstance();

        // Create Prompt And Response Model
        PromptAndResponseModel prModel = new PromptAndResponseModel();

        // Set cusotmer info type
        if (parameterValue.equalsIgnoreCase("PhoneNumber"))
        {
        	String phoneNumber="None";
        	phoneNumber = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,"PhoneNumber", "None");
            customerInfo.setCustomerInfoType(CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER);
            prModel.setResponseTypeNumeric();
            prModel.setMinLength(DomainGateway.getProperty("PhoneMinLength"));
            prModel.setMaxLength(DomainGateway.getProperty("PhoneMaxLength"));
            parameterValue = phoneNumber;//"Phone Number";
        }
        else
        if (parameterValue.equalsIgnoreCase("ZipCode"))
        {
        	String zipCode="None";
            zipCode = UIUtilities.retrieveText("Common",BundleConstantsIfc.PARAMETER_BUNDLE_NAME,"ZipCode", "None");
            customerInfo.setCustomerInfoType(CustomerInfoIfc.CUSTOMER_INFO_TYPE_POSTAL_CODE);
            prModel.setResponseTypeDefault();
            prModel.setMinLength(DomainGateway.getProperty("PostalCodeMinLength"));
            prModel.setMaxLength(DomainGateway.getProperty("PostalCodeMaxLength"));
            parameterValue = zipCode;//"Zip Code";
           
        }
        else
        {
            customerInfo.setCustomerInfoType(CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE);
        }

        // set customerInfo object to cargo
        cargo.setCustomerInfo(customerInfo);

        // if parameter is not set to none
        if(customerInfo.getCustomerInfoType() != CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE &&
                !cargo.getCanSkipCustomerPrompt())
        {
            prModel.setArguments(parameterValue);
            POSBaseBeanModel model = new POSBaseBeanModel();
            model.setPromptAndResponseModel(prModel);
            // show screen
            ui.showScreen(POSUIManagerIfc.PROMPT_CUSTOMER_INFO, model);
            
        }
        else
        {
            String letter = CommonLetterIfc.CONTINUE;
            if(cargo.getCanSkipCustomerPrompt())
            {
                letter = CommonLetterIfc.UNDO;
            }
            // parameter is set to none... continue without prompting
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }
}
