/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/ApplyMarkdownAmountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    acadar 11/02/08 - cleanup
 *    acadar 10/30/08 - localization of damage and markdown reason codes
 *    acadar 10/30/08 - localization of reason codes for item and transaction
 *                      discounts
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:30 PM  Robert Pearse
     $
     Revision 1.6  2004/03/22 18:35:04  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.5  2004/03/19 23:27:50  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review cleanup.

     Revision 1.4  2004/03/19 21:48:41  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review modifications. Further abstractions.

     Revision 1.3  2004/03/04 22:01:04  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Clear markdown items.

     Revision 1.2  2004/03/04 19:54:15  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Abstract method to get reason code list.

     Revision 1.1  2004/03/03 23:10:45  dcobb
     @scr 3911 Feature Enhancement: Markdown


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.pricing.markdown;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountAmountWithReasonSite;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 *   Apply the previously validated discounts by amount. <p>
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ApplyMarkdownAmountSite extends AbstractApplyDiscountAmountWithReasonSite
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Returns the discount basis.
     * @return DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL
     */
    //----------------------------------------------------------------------
    protected int getDiscountBasis()
    {
        return DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL;
    }

    //----------------------------------------------------------------------
    /**
     * Returns true if the discount is a damage discount.
     * @return false
     */
    //----------------------------------------------------------------------
    protected boolean isDamageDiscount()
    {
        return false;
    }

    //----------------------------------------------------------------------
    /**
     * Returns true if the discount is a markdown.
     * @return true
     */
    //----------------------------------------------------------------------
    protected boolean isMarkdown()
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
     *   Returns Identifiction for markdown by amount screen.  <P>
     *   @return  Screen model name
     */
    //----------------------------------------------------------------------
    public String getUIModel()
    {
        return POSUIManagerIfc.MARKDOWN_AMOUNT;
    }

    //----------------------------------------------------------------------
    /**
     *   Returns list of Reason Code data. <P>
     *   @param   cargo PricingCargo
     *   @return  reason code list
     *   @deprecated as of 13.1. Use {@link getLocalizedDiscountAmountCodeList(PricingCargo)
     */
    //----------------------------------------------------------------------
    /*public CodeListIfc getDiscountAmountCodeList(PricingCargo cargo)
    {
        return cargo.getMarkdownAmountCodeList();
    }*/

    /**
     *   Returns list of Reason Code data. <P>
     *   @param   cargo PricingCargo
     *   @return  reason code list
     *
    */
    public CodeListIfc getDiscountAmountCodeList(PricingCargo cargo)
    {
        return cargo.getLocalizedMarkdownAmountCodeList();
    }

}
