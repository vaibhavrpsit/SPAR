/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/PrintPassSite.java /main/16 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    sgu       05/11/11 - fix instant credit cargo to use the new reponse
 *                         object
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    glwang    02/06/09 - add isTrainingMode into
 *                         PrintableDocumentParameterBeanIfc
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 * 7    360Commerce 1.6         3/12/2008 12:34:41 PM  Deepti Sharma   changes
 *      to display house account number correctly
 * 6    360Commerce 1.5         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 5    360Commerce 1.4         8/24/2007 3:25:59 PM   Mathews Kochummen fix
 *      time format
 * 4    360Commerce 1.3         1/25/2006 4:11:38 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse
 *:
 * 4    .v700     1.2.1.0     11/4/2005 11:44:44     Jason L. DeLeau 4202: Fix
 *      extensibility issues for instant credit service
 * 3    360Commerce1.2         3/31/2005 15:29:30     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:24:23     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:13:26     Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:45  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Jan 15 2004 15:52:38   nrao
 * Formatted transaction information and repositioned it to appear below the header.
 *
 *    Rev 1.4   Jan 14 2004 15:32:44   nrao
 * Fixed printing type from slip printer to receipt. Fixed printer offline flow. Added DeviceException catch block.
 *
 *    Rev 1.3   Jan 13 2004 16:08:10   nrao
 * Added transaction information to House Account Enrollment Temporary Pass. Part of House Account Enroll rework.
 *
 *    Rev 1.2   Nov 24 2003 19:57:58   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

import java.util.Calendar;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.receipt.TempShoppingPass;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.utility.ValidationUtility;

import org.apache.log4j.Logger;

/**
 * @version $Revision: /main/16 $
 */
public class PrintPassSite extends SiteActionAdapter
{
    private static final long serialVersionUID = 3720508477373468130L;

    /**
     * Logger utility
     */
    private static final Logger logger = Logger.getLogger(PrintPassSite.class);

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();

        // get the input from the ui and set in cargo
        AuthorizeInstantCreditResponseIfc response = cargo.getInstantCreditResponse();
        InstantCreditIfc card = DomainGateway.getFactory().getInstantCreditInstance();

        // add information from authorizer response to card
        try
        {
            card = ValidationUtility.createInstantCredit(response, cargo.getCustomer(), cargo.getGovernmentId(), null);
        }
        catch (EncryptionServiceException ese)
        {
            logger.error("Could not encrypt house account number", ese);
        }

        // determine if in training mode
        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        Integer validFor = Integer.valueOf(14);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            validFor = pm.getIntegerValue(ParameterConstantsIfc.HOUSEACCOUNT_TempShoppingPassEnrollmentExp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        EYSDate expDate = DomainGateway.getFactory().getEYSDateInstance();
        expDate.add(Calendar.DAY_OF_YEAR, validFor);

        // build pass
        PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        TempShoppingPass pass = (TempShoppingPass)pdm.getParameterBeanInstance(ReceiptTypeConstantsIfc.TEMPSHOPPINGPASS);
        pass.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
        pass.setInstantCredit(card);
        pass.setEnrollmentTransaction(cargo.getTransaction());
        pass.setExpDate(expDate);
        pass.setTrainingMode(trainingMode);
        try
        {
            // print pass
            pdm.printReceipt((SessionBusIfc)bus, pass);

            bus.mail(letter, BusIfc.CURRENT);
        }
        catch (PrintableDocumentException e)
        {
            logger.error("unable to print temp shopping pass", e);
            // printing failed

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManager utility = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);
            String argText = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");
            String args[] = { argText };
            UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "RetryContinue", args);
        }
        catch (Exception e)
        {
            logger.error("unable to print temp shopping pass", e);
            // printing failed

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManager utility = (UtilityManager)bus.getManager(UtilityManagerIfc.TYPE);
            String argText = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");
            String args[] = { argText };
            UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "RetryContinue", args);
        }
    }
}