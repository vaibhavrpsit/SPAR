/**===========================================================================
 * ===========================================================================
 * $Log:
 *  9    .v12x      1.6.1.1     2/15/2008 2:28:03 PM   Jack G. Swan    Modified
 *        to remove code that maps values to the origninal price field.  This
 *       is now handled by a direct mapping from the sale return line item
 *       table.
 *  8    .v12x      1.6.1.0     8/27/2007 12:22:14 PM  Maisa De Camargo CR
 *       28585 - Fixed Discount Amount. It should always be positive in the
 *       RTLog.
 *  7    360Commerce 1.6         6/26/2007 4:41:16 PM   Maisa De Camargo Fixed
 *       Casting exception caused only when running in DB2.
 *  6    360Commerce 1.5         6/5/2007 7:02:08 PM    Maisa De Camargo Added
 *       Logic to update the TITEM record when price override. Added logic to
 *       retrieve the original price from the POSIdentity table. (This will be
 *        refactored after the Price Changes are completed).
 *  5    360Commerce 1.4         6/1/2007 4:55:50 PM    Maisa De Camargo Added
 *       logic to handle PriceOverride.
 *  4    360Commerce 1.3         5/29/2007 6:10:20 PM   Maisa De Camargo
 *       Refactored based on code review comments.
 *  3    360Commerce 1.2         5/25/2007 3:44:22 PM   Jack G. Swan    Cleanup
 *        quoted constants.
 *  2    360Commerce 1.1         5/16/2007 10:55:08 AM  Jack G. Swan    Added
 *       tender to loan, pickup (includes foreign currency), payin, and
 *       payout.  Added transaction header totals (includes foreign currency).
 *  1    360Commerce 1.0         5/3/2007 6:45:22 PM    Maisa De Camargo Added
 *       logic for the mapping the discounts.
 * $
 * ===========================================================================
 */
package max.retail.stores.exportfile.rtlog.fieldmappers;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import max.retail.stores.exportfile.rtlog.MAXRTLogMappingResultIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.exportfile.ExportFileException;
import oracle.retail.stores.exportfile.formater.FieldFormatIfc;
import oracle.retail.stores.exportfile.formater.RecordFormatIfc;
import oracle.retail.stores.exportfile.mapper.ColumnMapIfc;
import oracle.retail.stores.exportfile.mapper.EntityMapperIfc;
import oracle.retail.stores.exportfile.mapper.MappingResultIfc;
import oracle.retail.stores.exportfile.rtlog.fieldmappers.DiscountAmountMapper;
import oracle.retail.stores.xmlreplication.extractor.ReplicationExportException;
import oracle.retail.stores.xmlreplication.result.EntityIfc;
import oracle.retail.stores.xmlreplication.result.Row;

/**
 * This class maps the Discount Amount
 */
public class MAXDiscountAmountMapper extends DiscountAmountMapper {
	/**
	 * This class maps the UnitDiscountAmount. <BR>
	 * Item discounts are contained inside the Item Object. <BR>
	 * The UnitDiscountAmount is the RetailPriceModifier.Amount field (DB
	 * CO_MDFR_RTL_PRC.PE_MDFR_RT_PRC) / Quantity <BR>
	 * In case of a Price Override, we need to calculate the discount.
	 *
	 * @param columnValue
	 *            from the database
	 * @param row
	 *            currentRow from the database
	 * @param columnMap
	 *            mapping information
	 * @param field
	 *            output field
	 * @param record
	 *            output record
	 * @param entity
	 *            all info from database
	 * @param entityMapper
	 *            entity mapper
	 * @return int result
	 * @exception ExportFileException
	 */
	public int map(String columnValue, Row row, ColumnMapIfc columnMap, FieldFormatIfc field, RecordFormatIfc record,
			EntityIfc entity, EntityMapperIfc entityMapper) throws ExportFileException {
		BigDecimal unitDiscountAmount = new BigDecimal(columnValue);
		BigDecimal itemQuantity = new BigDecimal(record.getFieldFormat(QUANTITY_FIELD).getValue());
		// If this discount is a price override, map the reason code to the
		// TITEM record and set the IDISC record not export. ReSA does
		// not expect a IDISC record on a price override.
		if (isPriceOverride(row)) {
			mapReasonCodeWhenPriceOverride(row, record, entityMapper.getResults());
			record.setExportable(false);
		} else {
			int discountMethod = getDiscountMethod(row);
			// condition added for weighted item rule
			String reasonCode = getDiscountReasonCode(row);
			if (discountMethod == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE || discountMethod == 4  || (reasonCode != null && reasonCode.equals("5170")) ) {
				unitDiscountAmount = unitDiscountAmount.divide(itemQuantity, 4, BigDecimal.ROUND_HALF_UP).abs();
			}
			field.setValue(unitDiscountAmount.abs().toString());
		}
		return ColumnMapIfc.SUCCESS;
	}

	/**
	 * Get the discount method value from the the
	 * FIELD_PRICE_DERIVATION_RULE_METHOD_CODE column on the same row as the
	 * amount value.
	 * 
	 * @param row
	 * @return int discount method.
	 * @throws ExportFileException
	 */
	protected int getDiscountMethod(Row row) throws ExportFileException {
		int discountMethod = 0;
		try {
			discountMethod = Integer.parseInt(row.getFieldValueAsString(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE));
		} catch (ReplicationExportException e) {
			// Already logged; just throw an ExportFileException
			throw new ExportFileException("Could get discount method type.", e);
		}

		return discountMethod;
	}
	
	
		protected String getDiscountReasonCode(Row row) throws ExportFileException {
		String reasonCode = null;
		try {
			reasonCode = row.getFieldValueAsString("RC_MDFR_RT_PRC");
		} catch (ReplicationExportException e) {
			// Already logged; just throw an ExportFileException
			throw new ExportFileException("Could get discount reason code.", e);
		}

		return reasonCode;
	}

	/**
	 * Verify if the type of discount is Price override <BR>
	 * Price Override Method = 0 <BR>
	 * Discount Percentage Method = 1 <BR>
	 * Discount Amount Method = 2 <BR>
	 * Discount Fixed Price = 3
	 *
	 * @param row
	 *            currentRow from the database
	 * @return int result
	 * @exception ExportFileException
	 */
	protected boolean isPriceOverride(Row row) throws ExportFileException {
		boolean isPriceOverride = false;
		try {
			String discountMethod = row.getFieldValueAsString(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
			if (discountMethod.equals("0")) {
				isPriceOverride = true;
			}
		} catch (ReplicationExportException e) {
			String message = "Failed mapping the Discount Amount. Failure retrieving the field "
					+ FIELD_PRICE_DERIVATION_RULE_METHOD_CODE;
			throw new ExportFileException(message, e);
		}

		return isPriceOverride;
	}

	/**
	 * ReSA does not want a discount record for the price override; instead this
	 * method moves the price override reason code to the TITEM record. ReSA
	 * knows there is a price override from the reason code.
	 *
	 * @param row
	 *            currentRow from the database
	 * @param record
	 *            the current record
	 * @param result
	 *            the already mapped results
	 * @exception ExportFileException
	 */
	protected void mapReasonCodeWhenPriceOverride(Row row, RecordFormatIfc record, MappingResultIfc result)
			throws ExportFileException {
		RecordFormatIfc workingItemRecordFormat = getItemRecordFormat(row, result);

		if (workingItemRecordFormat != null) {
			FieldFormatIfc overrideReason = workingItemRecordFormat.getFieldFormat(OVERRIDE_REASON_FIELD);
			String overrideReasonCode = record.getFieldFormat(DISCOUNT_TYPE_FIELD).getValue();
			overrideReason.setValue(overrideReasonCode);
		}
	}

	/**
	 * Returns the ItemRecordFormat
	 *
	 * @param row
	 *            the current row
	 * @param result
	 *            the mapped results
	 * @return current workingItemRecordFormat
	 * @throws ExportFileException
	 */
	protected RecordFormatIfc getItemRecordFormat(Row row, MappingResultIfc result) throws ExportFileException {
		Collection itemRecords = ((MAXRTLogMappingResultIfc) result).getItemRecords();
		Iterator iterator = itemRecords.iterator();
		int i = 0;
		RecordFormatIfc workingItemRecordFormat = null;

		try {
			int lineItem = -1;
			lineItem = Integer.parseInt(row.getFieldValueAsString(FIELD_LINE_ITEM_SEQUENCE_NUMBER));

			boolean found = false;
			while (iterator.hasNext() && !found) {
				workingItemRecordFormat = (RecordFormatIfc) iterator.next();
				if (lineItem == i++)
					found = true;
			}
		} catch (Exception e) {
			String message = "Unable to find the current item.";
			logger.warn(message, e);
			throw new ExportFileException(message, e.getCause());
		}
		return workingItemRecordFormat;
	}
}
