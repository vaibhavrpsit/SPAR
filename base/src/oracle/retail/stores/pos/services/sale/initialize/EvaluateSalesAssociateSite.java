/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/EvaluateSalesAssociateSite.java /main/11 2011/02/16 09:13:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:56 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/04/19 14:50:34  rsachdeva
 *   @scr  3906 Sale
 *
 *   Revision 1.6  2004/03/31 21:32:16  rsachdeva
 *   @scr 3906 Sales Associate
 *
 *   Revision 1.5  2004/03/15 21:55:15  jdeleau
 *   @scr 4040 Automatic logoff after timeout
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:36   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:38   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

import java.io.Serializable;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * This site implements the IdentifySalesAssociateEveryTransaction parameter.
 * 
 * @version $Revision: /main/11 $
 */
public class EvaluateSalesAssociateSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7583051968107119000L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    protected static final String LETTER_IDENTIFY_SALES_ASSOCIATE = "AssociateID";

    /**
     * empty string
     */
    public static final String EMPTY_STRING = "";

    /**
     * Check the sales associate.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        String letterName       = CommonLetterIfc.CONTINUE;
        SaleCargoIfc cargo          = (SaleCargoIfc)bus.getCargo();


        // Default the sales associate to the operator
        cargo.setEmployee(cargo.getOperator());

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        boolean identifySalesAssoc = false;
        try
        {
            identifySalesAssoc = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_IdentifySalesAssociateEveryTransaction);
        }
        catch (ParameterException e)
        {
            logger.error(Util.throwableToString(e));
        }

        if (identifySalesAssoc)
        {
            letterName = LETTER_IDENTIFY_SALES_ASSOCIATE;
        }
        else
        {
            boolean defaultToCashier = true;
            try
            {
                defaultToCashier = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_DefaultToCashier);
            }
            catch (ParameterException e)
            {
                logger.error(Util.throwableToString(e));
            }
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            if (defaultToCashier)
            {
                EmployeeIfc associate = cargo.getEmployee();
                ui.salesAssociateNameChanged(associate.getPersonName().getFirstLastName());
            }
            else
            {
                //This is used only when IdentifySalesAssociateEveryTransaction is No and 
                //DefaultToCashier is also No. It is being set only for Display purpose 
                //to show Sales Associate as empty on receipts and journal.
                EmployeeIfc employee = DomainGateway.getFactory().getEmployeeInstance();
                //Initializing only to make sure employee id is empty
                //(in case, default is changed)
                employee.setEmployeeID(EMPTY_STRING);
                PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
                employee.setPersonName(name);
                RoleIfc role = DomainGateway.getFactory().getRoleInstance();
                employee.setRole(role);
                cargo.setEmployee(employee);
                ui.salesAssociateNameChanged(EMPTY_STRING);
            }
        }

        // Hey I timed out, I need to send the right signal back.
        if(cargo instanceof TimedCargoIfc)
        {
            if(((TimedCargoIfc)cargo).isTimeout())
            {
                letterName = "TimeoutComplete";
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
