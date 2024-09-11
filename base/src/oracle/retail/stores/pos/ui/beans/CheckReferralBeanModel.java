/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckReferralBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/08 22:14:54  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:16  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 20 2003 11:12:22   bwf
 * Updated for check auth.
 * Resolution for 3429: Check/ECheck Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:09:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   01 May 2002 16:11:06   baa
 * internationalization
 * Resolution for POS SCR-1624: Spanish translation
 * 
 *    Rev 1.2   01 May 2002 15:14:24   baa
 * internationalization, change big decimal for currency
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   25 Apr 2002 18:52:16   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

//----------------------------------------------------------------------------
/**
    Model for the CheckReferralBean
**/
//----------------------------------------------------------------------------
public class CheckReferralBeanModel extends POSBaseBeanModel
{
    // Revision number
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    // Authorization response
    String fieldAuthResponse = "";
    // Referral phone number
    String fieldReferralNumber = "";
    // Check amount
    CurrencyIfc fieldCheckAmount = DomainGateway.getBaseCurrencyInstance();
    // Approval code
    String fieldApprovalCode = "";

    //----------------------------------------------------------------------------
    /**
        Get the value of the AuthResponse field
        @return the value of AuthResponse
    **/
    //----------------------------------------------------------------------------
    public String getAuthResponse()
    {
        return fieldAuthResponse;
    }

    //----------------------------------------------------------------------------
    /**
        Get the value of the ReferralNumber field
        @return the value of ReferralNumber
    **/
    //----------------------------------------------------------------------------
    public String getReferralNumber()
    {
        return fieldReferralNumber;
    }

    //----------------------------------------------------------------------------
    /**
        Get the value of the CheckAmount field
        @return the value of CheckAmount
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getCheckAmount()
    {
        return fieldCheckAmount;
    }

    //----------------------------------------------------------------------------
    /**
        Get the value of the ApprovalCode field
        @return the value of ApprovalCode
    **/
    //----------------------------------------------------------------------------
    public String getApprovalCode()
    {
        return fieldApprovalCode;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the AuthResponse field
        @param the value to be set for AuthResponse
    **/
    //----------------------------------------------------------------------------
    public void setAuthResponse(String authResponse)
    {
        fieldAuthResponse = authResponse;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the ReferralNumber field
        @param the value to be set for ReferralNumber
    **/
    //----------------------------------------------------------------------------
    public void setReferralNumber(String referralNumber)
    {
        fieldReferralNumber = referralNumber;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the CheckAmount field
        @param the value to be set for CheckAmount
    **/
    //----------------------------------------------------------------------------
    public void setCheckAmount(CurrencyIfc checkAmount)
    {
        fieldCheckAmount = checkAmount;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the ApprovalCode field
        @param the value to be set for ApprovalCode
    **/
    //----------------------------------------------------------------------------
    public void setApprovalCode(String approvalCode)
    {
        fieldApprovalCode = approvalCode;
    }

    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: CheckReferralBeanModel Revision: " + revisionNumber + "\n");
        buff.append("AuthResponse [ " + fieldAuthResponse + "]\n");
        buff.append("ReferralNumber [ " + fieldReferralNumber + "]\n");
        buff.append("CheckAmount [ " + fieldCheckAmount + "]\n");
        buff.append("ApprovalCode [ " + fieldApprovalCode + "]\n");

        return(buff.toString());
    }
}
