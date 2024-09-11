/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ValidateSalesAssociateSite.java /main/16 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tzgarba   03/28/12 - Added check for null promptAndResponse model
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ohorne    08/24/09 - Scanned Employee ID can no longer exceed 10 chars
 *    djenning  03/28/09 - Enter a single line comment: creating
 *                         isSalesAssociateModifiedAtLineItem(), which is
 *                         similar to getSalesAssociateModified(), and using it
 *                         at receipt to determine whether to print the
 *                         SalesAssociate at the line item. Jack warned against
 *                         modifying the existing method as it is used for
 *                         something else.
 *    mahising  02/21/09 - Fixed issue for sales associate lable
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/2/2007 6:30:07 AM    Naveen Ganesh
 *         Corrected the message Sales Assoc. for CR 27977
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.12  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.11  2004/07/28 19:36:26  jdeleau
 *   @scr 4180 Make sure deprecated functionality stll works
 *
 *   Revision 1.10  2004/07/23 17:56:35  jdeleau
 *   @scr 4180 Remove deprecated call
 *
 *   Revision 1.9  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/04/20 13:17:05  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.7  2004/04/13 01:34:29  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.6  2004/03/23 14:51:13  tfritz
 *   @scr 4111 - An invalid employee ID is now added to an item's sales
 *   associated if the Sales Associate Validation parameter is N.
 *
 *   Revision 1.5  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.4  2004/03/11 19:36:33  fkane
 *   @scr 3984
 *   Added code to read the SalesAssociateValidation parameter to determine
 *   whether to validate the entered sales associate.
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Feb 10 2004 10:19:28   bwf
 * Updated with comments per code review.
 * 
 *    Rev 1.2   Feb 03 2004 16:51:10   bwf
 * Handle swiped employe id.
 * 
 *    Rev 1.1   Jan 27 2004 15:34:10   bwf
 * Update for sale associate multi item select.
 * Resolution for 3765: Modify Item Feature
 * 
 *    Rev 1.0   Aug 29 2003 16:01:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 08 2003 16:39:02   bwf
 * Retrieving correct text from bundle.
 * Resolution for 2241: INVALID_ASSOCIATE dialog screen does not match requirement
 * 
 *    Rev 1.2   Mar 05 2003 10:34:02   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 10 2002 08:26:38   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:17:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Mar 2002 16:44:26   cir
 * Added item id in the journal
 * Resolution for POS SCR-11: Modify Item Sales Assoc in wrong place on E Journal
 * 
 *    Rev 1.0   Mar 18 2002 11:37:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   04 Dec 2001 16:52:52   jbp
 * updates for modify item in release 5.0
 * Resolution for POS SCR-373: ModifyItem Updates
 *
 *    Rev 1.0   Sep 21 2001 11:29:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataManagerMsgIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Check the Sales Associate and send to proper screen
 * 
 * @version $Revision: /main/16 $
 */
public class ValidateSalesAssociateSite extends PosSiteActionAdapter
    implements ParameterConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1997091351118774942L;

    /**
     * Site name.
     */
    public static final String SITENAME = "ValidateSalesAssociateSite";

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    public static final int EMPLOYEE_ID_MAX_LENGTH = 10;

    /**
     * Checks the Sales Associate.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean employeeFound = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.ITEM_SALES_ASSC);
        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
        
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
        boolean validation = false;
        // check sales associate validation param        
        try
        {
            validation = pm.getBooleanValue(HOUSEACCOUNT_SalesAssociateValidation);
        }
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }
        
        String employeeID = null;
        // all employee enter screen now can be scanned and swiped.  this checks if the 
        // employee was swiped and if it is, it goes to the utility manager to retrieve
        // the employee id.
        if(pAndRModel != null && pAndRModel.isSwiped())
        {
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            employeeID = util.getEmployeeFromModel(pAndRModel);
        }
        else
        {
            employeeID = ui.getInput();
        }

        if(employeeID.length() > EMPLOYEE_ID_MAX_LENGTH)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidAssociateId");
            dialogModel.setType(DialogScreensIfc.ERROR);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else if (employeeID.length() > 0)
        {
            EmployeeIfc salesAssociate = null;
            // if parameter set to true, then lookup sales associate id
            /*
             * Lookup the employee ID in the database
             */
            try
            {
                EmployeeTransaction empTransaction = null;
                
                empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
                
                salesAssociate = empTransaction.getEmployee(employeeID);                    

            }
            catch (DataException de)
            {
                employeeFound = false;
                if (validation)
                {    
                    String[] msg = new String[3];
                    int error = de.getErrorCode();
                    UtilityManagerIfc utility =
                      (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                    switch(error)
                    {
                        case DataException.NO_DATA:
                        msg[0] = "";
                        msg[1] = utility.retrieveDialogText(DataManagerMsgIfc.NO_DATA_VALID_ID, 
                                                            DataManagerMsgIfc.NO_DATA_VALID_ID);
                        msg[2] = "";
                        break;
    
                        default:
                        msg[0] = utility.getErrorCodeString(error);
                        msg[1] = "";
                        msg[2] = DataManagerMsgIfc.CONTACT;
                        break;
                    }
    
                    DialogBeanModel dialogModel = new DialogBeanModel();
                    dialogModel.setResourceID("InvalidAssoc");
                    dialogModel.setType(DialogScreensIfc.ERROR);
                    dialogModel.setArgs(msg);
                    // display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
            }
            // If the Sales Associate Validation parameter is set to "N" and the sales associated was not found
            // then create an employee where the employee's name and ID is the entered sales associated ID
            if (!employeeFound && !validation)
            {
                employeeFound = true;
                PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
                salesAssociate = DomainGateway.getFactory().getEmployeeInstance();
                salesAssociate.setEmployeeID(employeeID);
                name.setFirstName(employeeID);
                salesAssociate.setPersonName(name);
            }
            if (employeeFound)
            {    
                //then set the item to the new value in the modified employee.
                JournalManagerIfc journal;
                journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                StringBuffer sb = new StringBuffer();
                int numItems = cargo.getItems().length;
                for(int i = 0;i < numItems;i++)
                {
                    cargo.getItems()[i].setSalesAssociate(salesAssociate);
                    cargo.getItems()[i].setSalesAssociateModifiedFlag(true);
                    
                    if(!cargo.getSalesAssociate().getEmployeeID().equals(salesAssociate.getEmployeeID()))
                    {
                    	cargo.getItems()[i].setSalesAssociateModifiedAtLineItem(true);
                    }
                    
                    if (journal != null)
                    {
                        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                        Object[] data = null;
                        sb.append(Util.EOL);
                        data = new Object[] { cargo.getItems()[i].getPLUItem().getItemID() };
                        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.ITEM_INQUIRY_ITEM_LABEL, data, journalLocale));
                        sb.append(Util.EOL);
                        
                        data = new Object[] { salesAssociate.getEmployeeID() };
                        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.SALES_ASSOCIATION, data, journalLocale));
                        sb.append(Util.EOL);
                    }
                    else
                    {
                        logger.error( "No journal manager found!");
                    }
                }
                
                if(journal != null)
                {
                    
                    //actually write the journal
                    journal.journal(cargo.getCashier().getEmployeeID(),
                            cargo.getTransactionID(),
                            sb.toString());
                }
                else
                {
                    logger.error( "No journal manager found!");
                }
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
            }
        }
    }
}
