/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TaxGroupInformationHolder.java /main/13 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/14 - set tax authority name
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

/**
 * @author mkp1
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TaxGroupInformationHolder
{
    public int taxAuthority;
    public String taxAuthorityName;
    public int taxType;
    public String taxRuleName;
    public String taxRuleDescription;
    public int compoundSequenceNumber;
    public boolean taxOnGrossAmountFlag;
    public int calculationMethodCode;
    public int taxRateUsageCode;
    public int roundingCode;
    public int roundingDigits;
    public boolean taxHoliday;
    public boolean inclusiveTaxFlag = false;	// by default, it is always false
}
