/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/TillPayrollPayOutCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    crain  02/18/10 - Forward Port: TILL PAYROLL PAYOUT
 *    abonda 01/03/10 - update header date
 *    ohorne 10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:29 AM   Anda D. Cadar   I18N
           merge
           
      3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:26:14 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
     $
     Revision 1.3  2004/07/22 04:56:57  khassen
     @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
     Adding database fields, print and reprint receipt functionality to reflect
     persistence of additional data in transaction.

     Revision 1.2  2004/03/16 18:30:47  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.1  2004/03/12 18:19:23  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.3  2004/02/12 16:50:04  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:47:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:58:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.6   Mar 05 2003 20:44:42   KLL
 * integration of code review results
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.4   Feb 12 2003 16:54:56   crain
 * Refactored getReasonCode()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.3   Feb 07 2003 15:10:16   crain
 * Replaced getCodeListMap()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.2   Jan 03 2003 08:38:20   KLL
 * Parameter control for number of receipts
 *
 *    Rev 1.1   Aug 29 2002 13:09:28   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:26:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:26   msg
 * Initial revision.
 *
 *    Rev 1.2   21 Jan 2002 17:50:54   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.1   21 Nov 2001 14:30:06   epd
 * removed redundant code
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:54   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TillCargo;

/**
 * 
 * @author khassen
 *
 * Cargo for the payroll pay out use case.
 */
public class TillPayrollPayOutCargo extends TillCargo
{

    private static final long serialVersionUID = 8845810397783973823L;
    
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
     * @deprecated As of Release 13.1. Use {@link #selectedLocalizedApprovalCode}
     */
    protected String approvalCodeString = utility.retrieveText("Common",BundleConstantsIfc.TILL_BUNDLE_NAME,"Unknown","Unknown");

    /**
     * Selected Reason Code
     */
    protected LocalizedCodeIfc selectedLocalizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

    /**
     * Selected Approval Code
     */
    protected LocalizedCodeIfc selectedLocalizedApprovalCode = DomainGateway.getFactory().getLocalizedCode();    
    
    protected CurrencyIfc amount;
    protected CodeListIfc reasonCodes = null;
    protected CodeListIfc approvalCodeList = null;
    protected String      employeeID;
    protected String      paidTo;
    protected String   [] addressLine = {"", "", ""};
    protected String      comments;
    protected boolean     employeeIDValidation = false;
    protected int         numSigLines;

    /**
     * Returns the amount set for the payroll pay out.
     * @return the currency amount.
     */
    public CurrencyIfc getAmount()
    {
        return(amount);
    }

    /**
     * Sets the amount for the payroll pay out.
     * @param a the currency amount.
     */
    public void setAmount(CurrencyIfc a)
    {
        amount = a;
    }

    /**
     * returns the access role for this transaction.
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TILL_PAYROLL_PAYOUT;
    }

    /**
     * @return the reason code list.
     */
    public CodeListIfc getReasonCodes()
    {
         return reasonCodes;
    }

    public void setReasonCodes(CodeListIfc codelist)
    {
        reasonCodes = codelist;
    }
    
    
    /**
     * Sets the selected reason code string.
     * @param value the reason code string.
     * @deprecated As of Release 13.1. Use {@link #setSelectedLocalizedReasonCode(LocalizedCodeIfc)
     */
    public void setSelectedReason(String value)
    {
            selectedReason = value;
    }

    /**
     * @return the selected reason code string.
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedReasonCode()
     */
    public String getSelectedReason()
    {
            return(selectedReason);
    }


    /**
     * Sets the selected reason code.
     * @param value the reason code (number).
     * @deprecated As of Release 13.1. Use {@link #setSelectedLocalizedReasonCode(LocalizedCodeIfc)
     */
    public void setSelectedReasonCode(String value)
    {
            selectedReasonCode = value;
    }

    /**
     * @return the selected reason code.
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedReasonCode()
     */
    public String getSelectedReasonCode()
    {
            return(selectedReasonCode);
    }
    
    /**
     * Sets the selected approval code.
     * @param value the approval code.
     * @deprecated As of Release 13.1. Use {@link #setSelectedLocalizedApprovalCode(LocalizedCodeIfc)
     */
    public void setSelectedApprovalCode(String value)
    {
        approvalCodeString = value;
    }

    /**
     * @return the approval code string.
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedApprovalCode()
     */
    public String getSelectedApprovalCode()
    {
        return(approvalCodeString);
    }

    /**
     * @return
     * @deprecated As of Release 13.1. Use {@link #getSelectedLocalizedApprovalCode()
     */
    public String getSelectedApprovalCodeID()
    {
        return getSelectedLocalizedApprovalCode().getCode();
    }

    /**
     * @return the approval code list.
     */
    public CodeListIfc getApprovalCodes()
    {
        return approvalCodeList;
    }

    
    public void setApprovalCodes(CodeListIfc codelist)
    {
        approvalCodeList = codelist;
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
    
    /**
     * @return the selectedApprovalCode
     */
    public LocalizedCodeIfc getSelectedLocalizedApprovalCode()
    {
        return selectedLocalizedApprovalCode;
    }

    /**
     * @param selectedApprovalCode the selectedApprovalCode to set
     */
    public void setSelectedLocalizedApprovalCode(LocalizedCodeIfc selectedApprovalCode)
    {
        this.selectedLocalizedApprovalCode = selectedApprovalCode;
    }
    
    /**
     * @return the paidTo string.
     */
    public String getPaidTo()
    {
        return (paidTo);
    }

    /**
     * Sets the paidTo string.
     * @param c the string to set it to.
     */
    public void setPaidTo(String c)
    {
        paidTo = c;
    }
    
    /**
     * @return gets the employee ID string.
     */
    public String getEmployeeID()
    {
        return employeeID;
    }
    public void setEmployeeID(String e)
    {
        employeeID = e;
    }
    
    /**
     * Sets the number of address lines to use
     * for receipt printing, etc.
     * @param i the number of lines.
     */
    public void setNumAddressLines(int i)
    {
        if (addressLine.length != i)
        {
            addressLine = new String[i];
        }
    }

    /**
     * @return the number of address lines used.
     */
    public int getNumAddressLines()
    {
        return addressLine.length;
    }
    
    /**
     * Returns a particular address line string.
     * @param i the index of the address line.
     * @return the address line string.
     */
    public String getAddressLine(int i)
    {
        if ((i > -1) && (i < addressLine.length))
        {
            return (addressLine[i]);
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets a particular address line.
     * @param i the index into the address line array.
     * @param a the string value to set to.
     */
    public void setAddressLine(int i, String a)
    {
        if ((i > -1) && (i < addressLine.length))
        {
            addressLine[i] = a;
        }
    }

    /**
     * @return  the comment string.
     */
    public String getComments()
    {
        return (comments);
    }

    /**
     * Sets the comment string.
     * @param c the comment string to set to.
     */
    public void setComments(String c)
    {
        comments = c;
    }

    /**
     * Determines whether or not to do validation of
     * employee ID.
     * @param b true if validation is to be done.  false
     * otherwise.
     */
    public void setValidateEmployeeID(boolean b)
    {
        employeeIDValidation = b;
    }

    /**
     * @return whether or not to do validation.
     */
    public boolean isValidateEmployeeID()
    {
        return employeeIDValidation;
    }

    /**
     * Sets the number of signature lines to be printed
     * on the store receipt.
     * @param i the number of signature lines.
     */
    public void setNumSigLines(int i)
    {
        numSigLines = i;
    }
    
    /**
     * @return the number of signature lines used.
     */
    public int getNumSigLines()
    {
        return numSigLines;
    }
}
