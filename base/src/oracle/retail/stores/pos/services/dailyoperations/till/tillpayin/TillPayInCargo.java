/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/TillPayInCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ohorne 11/03/08 - Localization of Till related Reason Codes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:29 AM   Anda D. Cadar   I18N
           merge
           
      3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
     $
     Revision 1.4  2004/03/12 18:15:32  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.3  2004/02/12 16:50:03  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:48:04  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:58:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.6   Mar 05 2003 20:44:40   KLL
 * integration of code review results
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.4   Feb 12 2003 16:52:10   crain
 * Refactored getReasonCodes()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.3   Jan 12 2003 16:03:56   pjf
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap(), setCodeListMap().
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.2   Jan 03 2003 08:37:06   KLL
 * Parameter control for number of receipts
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.1   Aug 29 2002 13:11:12   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:26:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:14   msg
 * Initial revision.
 *
 *    Rev 1.2   21 Jan 2002 17:50:52   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.1   21 Nov 2001 14:27:34   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:50   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TillCargo;

//------------------------------------------------------------------------------
/**
    Cargo for the Till PayIn service.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillPayInCargo extends TillCargo
{
    private static final long serialVersionUID = -74631956432013344L;

    protected static UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

    /**
     * @deprecated As of Release 13.1. Use {@link #selectedLocalizedReasonCode}
     */
    protected String selectedReason = utility.retrieveText("Common",BundleConstantsIfc.TILL_BUNDLE_NAME,"Unknown","Unknown");

    /**
     * selected reason code
     * @deprecated as of 13.1 Use {@link #selectedLocalizedReasonCode}
     */
    protected String selectedReasonCode;

    /**
     * Selected Reason Code
     */
    protected LocalizedCodeIfc selectedLocalizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
    
    /**
            PayIn Amount
    **/
    protected CurrencyIfc amount;

    /**
     * reason codes
     */
    protected CodeListIfc reasonCodes = null;

    
    protected int numSigLines;
    //--------------------------------------------------------------------------
    /**
        Returns the Till Pay In Amount<P>
        @return The amount
    */
    //--------------------------------------------------------------------------
    public CurrencyIfc getAmount()
    {
        return(amount);
    }

    //--------------------------------------------------------------------------
    /**
        Sets the Till In Amount. <P>
        @param a The amount
    */
    //--------------------------------------------------------------------------
    public void setAmount(CurrencyIfc a)
    {
        amount = a;
    }
    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TILL_PAYIN;
    }

    //--------------------------------------------------------------------------
    /**
        Returns array of reason codes. <P>
        @return array of reason codes
    **/
    //--------------------------------------------------------------------------
    public CodeListIfc getReasonCodes()
    {
        return reasonCodes;
    }

    /**
     * @param localizedReasonCode the localizedReasonCode to set
     */
    public void setReasonCodes(CodeListIfc reasonCodes)
    {
       this.reasonCodes = reasonCodes;
    }

    /**
     * Returns selected reason code text. <P>
     * @return selected reason code text
     * @deprecated As of Release 13.1. Use {@link #setSelectedLocalizedReasonCode(LocalizedCodeIfc)
     */
    public void setSelectedReason(String value)
    {
            selectedReason = value;
    }

    /**
     * Returns selected reason code text. <P>
     * @return selected reason code text
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedReasonCode()
     */
    public String getSelectedReason()
    {
            return(selectedReason);
    }

    /**
     * Returns string representation of selected reason code. <P>
     * @param value
     * @deprecated As of Release 13.1. Use {@link #setSelectedLocalizedReasonCode(LocalizedCodeIfc)
     */
    public void setSelectedReasonCode(String value)
    {
            selectedReasonCode = value;
    }

    /**
     * Returns string representation of selected reason code. <P>
     * @return string representation of selected reason code
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedReasonCode()
     */
    public String getSelectedReasonCode()
    {
            return(selectedReasonCode);
    }

    public void setNumSigLines(int i)
    {
        numSigLines = i;
    }
    
    public int getNumSigLines()
    {
        return numSigLines;
    }


    /**
     * @return the selectedReasonCode
     */
    public LocalizedCodeIfc getSelectedLocalizedReasonCode()
    {
        return selectedLocalizedReasonCode;
    }

    /**
     * @param selectedReasonCode the selectedReasonCode to set
     */
    public void setSelectedLocalizedReasonCode(LocalizedCodeIfc selectedReasonCode)
    {
        this.selectedLocalizedReasonCode = selectedReasonCode;
    }

}
