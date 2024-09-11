/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/CheckAccountabilitySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/13 14:10:00  awilliam
 *   @scr 4314 selcting Service Alert crashes Pos
 *
 *   Revision 1.1  2004/03/15 16:51:06  baa
 *   @scr 0 Move deprecated pos files.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 28 2004 13:54:48   rsachdeva
 * Deprecated
 * 
 *    Rev 1.0   Aug 29 2003 16:04:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:08:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   17 Jan 2002 14:39:12   jbp
 * removed unused letter
 * Resolution for POS SCR-715: Unable to login to Crossreach.
 *
 *    Rev 1.0   Sep 21 2001 11:33:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// java imports
import java.io.Serializable;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    This site checks the accountability for Register or Cashier and does
    the appropriate processing.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated as of release 7.0 The complete pos service was replaced by the sale service under _360commerce
**/
//--------------------------------------------------------------------------
public class CheckAccountabilitySite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected static final String LETTER_OPERATOR_ID = "OperatorID";

    //----------------------------------------------------------------------
    /**
       Checks the accountability.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        boolean callOperatorID = false;
        String letterName = null;

        ServiceAlertCargo cargo = (ServiceAlertCargo)bus.getCargo();

        // Clear ui values
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        statusModel.setCashierName("");
        statusModel.setSalesAssociateName("");
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

        // Clear transaction information
        cargo.setTransaction(null);
        cargo.resetOriginalReturnTransactions();
        cargo.setIndex(-1);

        RegisterIfc register = cargo.getRegister();

        if (cargo.isPasswordRequired())
        {
            callOperatorID = true;
        }
        else if (register.getAccountability()
                 == AbstractFinancialEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            /*
             * Check IdentifyCashierEveryTransaction parameter first
             */
            ParameterManagerIfc pm;
            pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            try
            {
                Serializable[] values;
                values = pm.getParameterValues("IdentifyCashierEveryTransaction");

                String parameterValue = (String)values[0];
                if (parameterValue.equalsIgnoreCase("Y"))
                {
                    callOperatorID = true;
                }
            }
            catch (ParameterException e)
            {
                logger.error( "" + Util.throwableToString(e) + "");
            }

            /*
             * See if there is more than one open till
             */
            if (!callOperatorID)
            {
                TillIfc[] tills = register.getTills();
                int openTills = 0;

                if (tills != null)
                {
                    for (int i = 0; i < tills.length; ++i)
                    {
                        if (tills[i].getStatus() == TillIfc.STATUS_OPEN)
                        {
                            ++openTills;

                            /*
                             * If there is only one open till,
                             * this will default the proper cashier
                             */
                            cargo.setOperator(tills[i].getSignOnOperator());
                        }
                    }
                }

                if (openTills > 1)
                {
                    // more than one open till, we need to identify the operator
                    callOperatorID = true;
                }
            }
        }
        else if (register.getAccountability()
                 == AbstractFinancialEntityIfc.ACCOUNTABILITY_REGISTER)
        {
            //Requirements ask for password required in this mode
            cargo.setPasswordRequired(true);
            callOperatorID = true;
        }

        if (callOperatorID)
        {
            letterName = LETTER_OPERATOR_ID;
            
        }
        else
        {
            letterName = CommonLetterIfc.CONTINUE;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  CheckAccountabilitySite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
