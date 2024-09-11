/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/EnterTillPayInAmountSite.java /main/13 2012/08/27 11:22:44 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 08/16/12 - wptg - removed placeholder from key
 *                      PromptAndResponsePanelSpec.PayInPrompt
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    ohorne 11/03/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================

     $Log:
      5    .v8x      1.3.1.0     3/9/2007 1:05:07 PM    Brett J. Larsen CR 4530
            - when default reason code is designated, it is not displayed

           setting model's default reason code for bean to later display
      4    360Commerce1.3         7/28/2006 5:40:06 PM   Brett J. Larsen 4530:
           default reason code fix
           v7x->360Commerce merge
      3    360Commerce1.2         3/31/2005 4:28:05 PM   Robert Pearse   
      2    360Commerce1.1         3/10/2005 10:21:28 AM  Robert Pearse   
      1    360Commerce1.0         2/11/2005 12:10:55 PM  Robert Pearse   
     $

      4    .v7x      1.2.1.0     6/23/2006 4:49:51 AM   Dinesh Gautam   CR
           4530: Fix for reason code

     Revision 1.9  2004/07/22 00:06:34  jdeleau
     @scr 3665 Standardize on I18N standards across all properties files.
     Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.

     Revision 1.8  2004/07/14 18:47:09  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.7  2004/06/15 02:20:26  aschenk
     @scr 5506 - Changed the reason code drop down to not have a default.

     Revision 1.6  2004/04/28 20:25:38  cdb
     @scr 4572 Addressing incorrect implementation of item 5 for PayInPayOutReceiptSignatureLinePrintingParameter

     Revision 1.5  2004/03/15 14:37:10  khassen
     Pay in/out revisions - additional comments.

     Revision 1.4  2004/03/12 18:15:32  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.3  2004/02/12 16:50:03  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:48:04  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:58:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 13 2003 10:54:10   HDyer
 * Made change for beanModel setReasonCodes signature change.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:26:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 09 2002 17:17:30   mpm
 * Text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:19:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:52   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

//java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterTillPayInBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    <P>
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class EnterTillPayInAmountSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 871957382070217847L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //--------------------------------------------------------------------------
    /**
        EnterTillPayInAmountSite
    **/
    //--------------------------------------------------------------------------
    public static final String SITENAME = "EnterTillPayInAmountSite";

    //--------------------------------------------------------------------------
    /**
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //get till info from cargo
        TillPayInCargo cargo = (TillPayInCargo)bus.getCargo();

        //set model with data
        EnterTillPayInBeanModel model = new EnterTillPayInBeanModel();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        //set Prompt and Response arg
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        model.setPromptAndResponseModel(parModel);
        
        // Set the reason codes in the model from the cargo.
        CodeListIfc reasonCodes = utility.getReasonCodes(cargo.getOperator().getStoreID(),
                                                         CodeConstantsIfc.CODE_LIST_TILL_PAY_IN_REASON_CODES );
        cargo.setReasonCodes(reasonCodes);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        model.inject(reasonCodes, CodeConstantsIfc.CODE_UNDEFINED, lcl);
        
        // Get the number of signature lines that ought to be visible
        // on the receipt.
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        String numSigLinesString = util.getParameterValue("PayInPayOutReceiptSignatureLinePrinting", "OneSignatureLine");
        boolean isOneLine  = numSigLinesString.equals("OneSignatureLine");
        boolean isTwoLines = numSigLinesString.equals("TwoSignatureLines");
        if (isOneLine)
        {
            cargo.setNumSigLines(1);
        }
        else if (isTwoLines)
        {
            cargo.setNumSigLines(2);
        }
        else
        {
            cargo.setNumSigLines(0);
        }

        //show screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.setModel(POSUIManagerIfc.PAY_IN, model);
        ui.showScreen(POSUIManagerIfc.PAY_IN);
    }
 }
