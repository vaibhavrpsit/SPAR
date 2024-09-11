/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for Charge Request (POS_Amazon Pay Integration )
 *  Rev	1.1 	June 20, 2019		Purushotham Reddy 	Changes for Verify Request (POS_Amazon Pay Integration )
 *  Rev	1.1 	June 30, 2019		Purushotham Reddy 	Changes for Refund Request (POS_Amazon Pay Integration )
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
 @author Purushotham Reddy Sirison
 **/

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.tender.amazonpay.MAXAmazonPayTenderConstants;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.certicom.net.ssl.internal.HttpURLConnection;

import weblogic.utils.collections.TreeMap;

public class SignatureUtil {

	private static final String ALGORITHM = "AWS4-HMAC-SHA384";
	private static final String SHA_384 = "SHA-384";
	private static final String HTTPREUESTMETHOD = "POST";
	private static final String DATE_TIME_FORMAT = "YYYYMMdd'T'HHmmss'Z'";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String NEW_LINE_CHARACTER = "\n";
	private static final String SERVICE_NAME = "AmazonPay";
	private static final String HMAC_ALGORITHM = "HmacSHA384";
	private static final String URL = "/eu-west-1/AmazonPay/aws4_request";
	//public static String signature=null;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.tender.wallet.amazonpay.SignatureUtil.class);

	private static final String merchantID = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYMERCHANTID);

	protected static final String accessKeyId = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYACCESSKEYID);
	
	protected static final String secretKey = MAXAmazonPayConfig
			.get(MAXAmazonPayTenderConstants.AMAZONPAYSECRETKEY);
	
	public static String  signature="";
	public static String  signature2="";
	public static String  signatureResponse="";
	public static String  signatureResponseRefund="";
	public static String  signatureVerifyResponse="";
	

	private static Mac mac;

	static {
		try {
			mac = Mac.getInstance(HMAC_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	static byte[] HmacSHA384(String data, byte[] key) throws Exception {
		try {
			mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));

		} catch (Exception e) {
			throw new Exception("Invalid key exception while mac init", e);
		}
		return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	}

	protected static String genarateSignatureForCharge(MAXTenderCargo cargo,
			String url, String totalAmount, Map<String, String> storeDetails,
			Long currentTime, String tranId) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForCharge(cargo, url,
				totalAmount, storeDetails, currentTime, tranId);

		String stringToSign = genarateStringToSignForCharge(canonical, url,
				currentTime);

		String dateStamp = myFormatNew.format(new Date(currentTime)).toString();

		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",SERVICE_NAME);

		 signature = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForCharge : " + signature);
		
		return signature;

	}

	protected static String genarateSignatureForVerify(MAXTenderCargo cargo,
			String url, Long currentTime, String tranId) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForVerify(cargo, url,
				currentTime, tranId);

		String stringToSign = genarateStringToSignForVerify(canonical, url,
				currentTime);

		String dateStamp = myFormatNew.format(new Date(currentTime)).toString();

		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",
				SERVICE_NAME);

		String signature = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForVerify : " + signature);
		return signature;

	}

	protected static String genarateSignatureForRefund(MAXTenderCargo cargo,
			String url, String totalAmount, Long currentTime, String amazonPayTranId, String tranId) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForRefund(cargo, url,
				totalAmount, currentTime, amazonPayTranId, tranId);

		String stringToSign = genarateStringToSignForRefund(canonical, url,
				currentTime);

		String dateStamp = myFormatNew.format(new Date(currentTime)).toString();

		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",
				SERVICE_NAME);

		 signature2 = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForRefund : " + signature2);
		//System.out.println("signature2="+signature2);
		return signature2;

	}

	static byte[] getSigningKey(String key, String dateStamp,
			String regionName, String serviceName) throws Exception {
		byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
		byte[] kDate = HmacSHA384(dateStamp, kSecret);
		byte[] kRegion = HmacSHA384(regionName, kDate);
		byte[] kService = HmacSHA384(serviceName, kRegion);
		byte[] kSigning = HmacSHA384("aws4_request", kService);
		return kSigning;
	}

	private static String genarateStringToSignForCharge(
			String canonicalRequest, String url, Long currentTime)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForCharge : " + stringToSign);
		return stringToSign.toString();
	}

	private static String genarateStringToSignForVerify(
			String canonicalRequest, String url, Long currentTime)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForVerify : " + stringToSign);
		return stringToSign.toString();
	}

	private static String genarateStringToSignForRefund(
			String canonicalRequest, String url, Long currentTime)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForRefund : " + stringToSign);
		return stringToSign.toString();
	}

	private static String getHashedCanonicalRequest(String canonicalRequest)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(SHA_384);
		//md.reset();
		byte[] messageDigest = md.digest(canonicalRequest.getBytes(StandardCharsets.UTF_8));
		BigInteger no = new BigInteger(1, messageDigest);
		String hashtext = no.toString(16); 
		while (hashtext.length() < 32) { 
            hashtext = "0" + hashtext; 
        } 
		 return hashtext; 
		//md.update(canonicalRequest.getBytes(StandardCharsets.UTF_8));
		//return Hex.encodeHex(md.digest()).toString();
	}

	private static String genarateCanonicalStringForCharge(
			MAXTenderCargo cargo, String url, String totalAmount,
			Map<String, String> storeDetails, Long currentTime, String tranId) throws UnknownHostException {

		StringBuilder cannonicalRequest = new StringBuilder();

		String hostname = url.substring(8);
		InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();
		String storeid=cargo.getStoreStatus().getStore().getStoreID();

		cannonicalRequest.append(HTTPREUESTMETHOD).append(NEW_LINE_CHARACTER)
				.append(hostname).append(NEW_LINE_CHARACTER)
				.append((NEW_LINE_CHARACTER))
		.append("x-amz-algorithm=").append(ALGORITHM)
		.append("&")
		.append("x-amz-client-id=").append(merchantID)
		.append("&")
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())
		.append(NEW_LINE_CHARACTER);
		
		
		

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		formattedRequestParameters.put(MAXAmazonPayTenderConstants.AMOUNT,
				totalAmount);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CUSTOMERIDTYPEFIELD,
				MAXAmazonPayTenderConstants.CUSTOMERIDTYPE);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.INTENTFIELD,
				MAXAmazonPayTenderConstants.INTENTVALUE);
		
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONPAYCODEFIELD,
				cargo.getBarcode());

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);
*/
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.STOREDETAIL, storeDetails);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD, merchantID);

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD, accessKeyId);
*/
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CHARGEID, tranId );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SELLERNOTE, storeid );
		
		
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.ATTPROGFIELD,
				MAXAmazonPayTenderConstants.ATTPROGRAM);
		

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentDate);
		*/
		
		
		

		cannonicalRequest.append(formatParameters(formattedRequestParameters));

		logger.info("cannonicalRequestForCharge : "
				+ cannonicalRequest.toString());
		return cannonicalRequest.toString();
	}

	private static String genarateCanonicalStringForRefund(
			MAXTenderCargo cargo, String url, String totalAmount,
			Long currentTime, String amazonPayTranId, String tranId) throws UnknownHostException {

		StringBuilder cannonicalRequestForRefund = new StringBuilder();

		String hostname = url.substring(8);
         InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();

		String storeid=cargo.getStoreStatus().getStore().getStoreID();
		cannonicalRequestForRefund.append(HTTPREUESTMETHOD)
				.append(NEW_LINE_CHARACTER).append(hostname)
				.append(NEW_LINE_CHARACTER)
				.append(NEW_LINE_CHARACTER)
				.append("x-amz-algorithm=").append(ALGORITHM)
				.append("&")
				.append("x-amz-client-id=").append(merchantID)
				.append("&")
				.append("x-amz-date=").append(currentDate)
				.append("&")
				.append("x-amz-expires=").append("900")
				.append("&")
				.append("x-amz-source=").append("Browser")
				.append("&")
				.append("x-amz-user-agent=").append("Postman")
				.append("&")
				.append("x-amz-user-ip=").append(myIP.getHostAddress())
				.append(NEW_LINE_CHARACTER);

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CHARGEIDTYPEFIELD, MAXAmazonPayTenderConstants.CHARGEIDTYPE);

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONTRASACTIONIDTYPEFIELD,
				MAXAmazonPayTenderConstants.AMAZONTRASACTIONIDTYPE);*/

		formattedRequestParameters.put(MAXAmazonPayTenderConstants.AMOUNT,
				totalAmount);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.REFUNDREFERENCEID, tranId);

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);*/

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD, merchantID);

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD, accessKeyId);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentTime.toString());*/
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTFIELD,
				MAXAmazonPayTenderConstants.AMAZONPAYREFUNDSOFTDESC);
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CHARGEID, tranId );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SELLERNOTE, storeid);

		cannonicalRequestForRefund
				.append(formatParameters(formattedRequestParameters));
		

		logger.info("cannonicalRequestForRefund : "
				+ cannonicalRequestForRefund.toString());
		return cannonicalRequestForRefund.toString();
	}

	private static String genarateCanonicalStringForVerify(
			MAXTenderCargo cargo, String url, Long currentTime, String tranId) throws UnknownHostException {

		StringBuilder cannonicalRequest = new StringBuilder();

		String hostname = url.substring(8);
        InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		String currentDate = myFormatNew.format(new Date(currentTime))
				.toString();

		cannonicalRequest.append("GET").append(NEW_LINE_CHARACTER)
				.append(hostname).append(NEW_LINE_CHARACTER);
		/*.append("x-amz-client-id=").append(merchantID)
		.append("&")
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())
		.append(NEW_LINE_CHARACTER);*/

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD, merchantID);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDTYPEFIELD,
				MAXAmazonPayTenderConstants.MERCHANTTRANSACTIONIDTYPE);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.TRANSACTIONIDFIELD, tranId);

		/*formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREMETHODFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREMETHOD);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.SIGNATUREVERSIONFIELD,
				MAXAmazonPayTenderConstants.SIGNATUREVERSION);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.ACCESSKEYIDFIELD, accessKeyId);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.TIMSSTAMPFIELD,
				currentTime.toString());
*/
		cannonicalRequest.append(formatParameters(formattedRequestParameters)).append(NEW_LINE_CHARACTER)
		.append("x-amz-algorithm=").append(ALGORITHM)
		.append("&")
		.append("x-amz-client-id=").append(merchantID)
		.append("&")
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())
		.append(NEW_LINE_CHARACTER);
;

		logger.info("CanonicalRequestForVerify : "
				+ cannonicalRequest.toString());
		return cannonicalRequest.toString();
	}

	private static String formatParameters(final Map<String, Object> parameters) {
		Map<String, Object> sorted = new TreeMap<String, Object>();
		sorted.putAll(parameters);
		Iterator<Map.Entry<String, Object>> pairs = sorted.entrySet()
				.iterator();
		StringBuilder queryStringBuilder = new StringBuilder();
		while (pairs.hasNext()) {
			Map.Entry<String, Object> pair = pairs.next();
			String key = pair.getKey();
		//	queryStringBuilder.append("\"");
			queryStringBuilder.append(percentEncodeRfc3986(key));
			queryStringBuilder.append("=");
			String value = pair.getValue().toString();
			queryStringBuilder.append(percentEncodeRfc3986(value));
			if (pairs.hasNext()) {
				queryStringBuilder.append("&");
				//queryStringBuilder.append("\",");
			}
		}
		return queryStringBuilder.toString();
	}

	private static String percentEncodeRfc3986(final String s) {
		String out;
		try {
			out = URLEncoder.encode(s, StandardCharsets.UTF_8.name())
					.replace("+", "%20").replace("*", "%2A")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return out;
	}
	//vaibhav charge response cannonical request
	
	protected static String genarateSignatureForChargeResponse(MAXTenderCargo cargo,
			String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForChargeResponse(cargo, url,amazonPayResponse,connection);

		String stringToSign = genarateStringToSignForChargeResponse(canonical, url,connection);

		//String dateStamp = myFormatNew.format(new Date()).toString();
		
          /*String dateStamp= connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,dateStamp.lastIndexOf("]"));*/
             String dateStamp = connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,9);
		    
        		  
		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",SERVICE_NAME);

		 signatureResponse = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForChargeResponse : " + signatureResponse);
		
		return signatureResponse;

	}
	
	private static String genarateStringToSignForChargeResponse(
			String canonicalRequest, String url,java.net.HttpURLConnection connection)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		/*String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();*/
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String requestDateTime = connection.getHeaderFields().get("x-amz-date").toString();
		requestDateTime=requestDateTime.substring(1,requestDateTime.lastIndexOf("]"));
		String currentDate = connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,9);
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForChargeResponse :" + stringToSign);
		return stringToSign.toString();
	}
	
	private static String genarateCanonicalStringForChargeResponse(
			MAXTenderCargo cargo, String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws UnknownHostException {

		StringBuilder cannonicalRequest = new StringBuilder();

		String hostname = url.substring(8);
		//InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String currentDate=connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,currentDate.lastIndexOf("]"));
		
		String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
	     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
				
		String storeid=cargo.getStoreStatus().getStore().getStoreID();

		cannonicalRequest.append(HTTPREUESTMETHOD).append(NEW_LINE_CHARACTER)
				.append(hostname).append(NEW_LINE_CHARACTER)
				.append((NEW_LINE_CHARACTER))
		.append("x-amz-algorithm=").append(ALGORITHM)
		.append("&")
		/*.append("x-amz-client-id=").append(merchantID)
		.append("&")*/
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-request-id=").append(requestid)
		/*.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())*/
		.append(NEW_LINE_CHARACTER);
		
		
		

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		//formattedRequestParameters.put(MAXAmazonPayTenderConstants.AMOUNT,
			//	totalAmount);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD, merchantID);

		
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CHARGEID, amazonPayResponse.getOrderId());
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONCHARGEID, amazonPayResponse.getAmazonTransactionId() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONAPPROVEDAMOUNT, amazonPayResponse.getApprovedAmount() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONREQUESTEDAMOUNT, amazonPayResponse.getAmount() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.STATUS, amazonPayResponse.getStatus() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CREATETIME, amazonPayResponse.getCreateTime() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.UPDATETIME, amazonPayResponse.getUpdateTime() );

		cannonicalRequest.append(formatParameters(formattedRequestParameters));

		logger.info("cannonicalRequestForChargeResponse : "
				+ cannonicalRequest.toString());
		return cannonicalRequest.toString();
	}

	protected static String genarateSignatureForRefundResponse(MAXTenderCargo cargo,
			String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForRefundResponse(cargo, url,amazonPayResponse,connection);

		String stringToSign = genarateStringToSignForRefundResponse(canonical, url,connection);

		//String dateStamp = myFormatNew.format(new Date()).toString();
		
          /*String dateStamp= connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,dateStamp.lastIndexOf("]"));*/
             String dateStamp = connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,9);
		    
        		  
		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",SERVICE_NAME);

		signatureResponseRefund = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForRefundResponse : " + signatureResponseRefund);
		
		return signatureResponseRefund;

	}
	
	private static String genarateStringToSignForRefundResponse(
			String canonicalRequest, String url,java.net.HttpURLConnection connection)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		/*String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();*/
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String requestDateTime = connection.getHeaderFields().get("x-amz-date").toString();
		requestDateTime=requestDateTime.substring(1,requestDateTime.lastIndexOf("]"));
		String currentDate = connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,9);
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForRefundResponse :" + stringToSign);
		return stringToSign.toString();
	}
	
	private static String genarateCanonicalStringForRefundResponse(
			MAXTenderCargo cargo, String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws UnknownHostException {

		StringBuilder cannonicalRequest = new StringBuilder();

		String hostname = url.substring(8);
		//InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String currentDate=connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,currentDate.lastIndexOf("]"));
		
		String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
	     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
				
		String storeid=cargo.getStoreStatus().getStore().getStoreID();

		cannonicalRequest.append(HTTPREUESTMETHOD).append(NEW_LINE_CHARACTER)
				.append(hostname).append(NEW_LINE_CHARACTER)
				.append((NEW_LINE_CHARACTER))
		.append("x-amz-algorithm=").append(ALGORITHM)
		.append("&")
		/*.append("x-amz-client-id=").append(merchantID)
		.append("&")*/
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-request-id=").append(requestid)
		/*.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())*/
		.append(NEW_LINE_CHARACTER);
		
		
		

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		//formattedRequestParameters.put(MAXAmazonPayTenderConstants.AMOUNT,
			//	totalAmount);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.REFUNDREFERENCEID,
				amazonPayResponse.getWalletTxnId().substring(0, 26));
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONREFUNDID,
				amazonPayResponse.getAmazonTransactionId());
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMOUNT,
				amazonPayResponse.getAmount());
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.REFUNDFEE,
				amazonPayResponse.getRefundFee());	
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.STATUS, amazonPayResponse.getStatus() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CREATETIME, amazonPayResponse.getCreateTime() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.UPDATETIME, amazonPayResponse.getUpdateTime() );

		cannonicalRequest.append(formatParameters(formattedRequestParameters));

		logger.info("cannonicalRequestForRefundResponse : "
				+ cannonicalRequest.toString());
		return cannonicalRequest.toString();
	}

	
	protected static String genarateSignatureForVerifyResponse(MAXTenderCargo cargo,
			String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws Exception {

		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));

		String canonical = genarateCanonicalStringForVerifyResponse(cargo, url,amazonPayResponse,connection);

		String stringToSign = genarateStringToSignForVerifyResponse(canonical, url,connection);

		//String dateStamp = myFormatNew.format(new Date()).toString();
		
          /*String dateStamp= connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,dateStamp.lastIndexOf("]"));*/
             String dateStamp = connection.getHeaderFields().get("x-amz-date").toString();
             dateStamp=dateStamp.substring(1,9);
		    
        		  
		byte[] signingKey = getSigningKey(secretKey, dateStamp, "eu-west-1",SERVICE_NAME);

		 signatureVerifyResponse = DatatypeConverter
				.printBase64Binary(HmacSHA384(stringToSign, signingKey))
				.replace("+", "-").replace("/", "_");

		logger.info("SignatureForVerifyResponse : " + signatureVerifyResponse);
		
		return signatureVerifyResponse;

	}
	
	private static String genarateStringToSignForVerifyResponse(
			String canonicalRequest, String url,java.net.HttpURLConnection connection)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		SimpleDateFormat myFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_FORMAT);
		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
		/*String requestDateTime = myFormat.format(new Date(currentTime))
				.toString();*/
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String requestDateTime = connection.getHeaderFields().get("x-amz-date").toString();
		requestDateTime=requestDateTime.substring(1,requestDateTime.lastIndexOf("]"));
		String currentDate = connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,9);
		String credentialScope = currentDate + URL;

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(ALGORITHM).append(NEW_LINE_CHARACTER);
		stringToSign.append(requestDateTime).append(NEW_LINE_CHARACTER);

		stringToSign.append(credentialScope).append(NEW_LINE_CHARACTER);

		String hashedCannReq = getHashedCanonicalRequest(canonicalRequest);
		stringToSign.append(hashedCannReq);
		logger.info("StringToSignForVerifyResponse :" + stringToSign);
		return stringToSign.toString();
	}
	
	private static String genarateCanonicalStringForVerifyResponse(
			MAXTenderCargo cargo, String url,MAXAmazonPayResponse amazonPayResponse,java.net.HttpURLConnection connection) throws UnknownHostException {

		StringBuilder cannonicalRequest = new StringBuilder();

		String hostname = url.substring(8);
		//InetAddress myIP=InetAddress.getLocalHost();
		
		SimpleDateFormat myFormatNew = new SimpleDateFormat(DATE_TIME_FORMAT);

		myFormatNew.setTimeZone(TimeZone.getTimeZone("UTC"));
	
		//String currentDate = myFormatNew.format(new Date(currentTime))
				//.toString();
		String currentDate=connection.getHeaderFields().get("x-amz-date").toString();
		currentDate=currentDate.substring(1,currentDate.lastIndexOf("]"));
		
		String requestid=connection.getHeaderFields().get("x-amz-request-id").toString();
	     requestid=requestid.substring(1,requestid.lastIndexOf("]"));
				
		String storeid=cargo.getStoreStatus().getStore().getStoreID();

		cannonicalRequest.append(HTTPREUESTMETHOD).append(NEW_LINE_CHARACTER)
				.append(hostname).append(NEW_LINE_CHARACTER)
				.append((NEW_LINE_CHARACTER))
		.append("x-amz-algorithm=").append(ALGORITHM)
		.append("&")
		/*.append("x-amz-client-id=").append(merchantID)
		.append("&")*/
		.append("x-amz-date=").append(currentDate)
		.append("&")
		.append("x-amz-request-id=").append(requestid)
		/*.append("x-amz-expires=").append("900")
		.append("&")
		.append("x-amz-source=").append("Browser")
		.append("&")
		.append("x-amz-user-agent=").append("Postman")
		.append("&")
		.append("x-amz-user-ip=").append(myIP.getHostAddress())*/
		.append(NEW_LINE_CHARACTER);
		
		
		

		Map<String, Object> formattedRequestParameters = new HashMap<String, Object>();

		//formattedRequestParameters.put(MAXAmazonPayTenderConstants.AMOUNT,
			//	totalAmount);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CURRENCYCODE,
				MAXAmazonPayTenderConstants.CURRENCY);

		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.MERCHANTGUIDFIELD, merchantID);

		
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CHARGEID, amazonPayResponse.getOrderId());
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONCHARGEID, amazonPayResponse.getAmazonTransactionId() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONAPPROVEDAMOUNT, amazonPayResponse.getApprovedAmount() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.AMAZONREQUESTEDAMOUNT, amazonPayResponse.getAmount() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.STATUS, amazonPayResponse.getStatus() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.CREATETIME, amazonPayResponse.getCreateTime() );
		formattedRequestParameters.put(
				MAXAmazonPayTenderConstants.UPDATETIME, amazonPayResponse.getUpdateTime() );

		cannonicalRequest.append(formatParameters(formattedRequestParameters));

		logger.info("cannonicalRequestForVerifyResponse : "
				+ cannonicalRequest.toString());
		return cannonicalRequest.toString();
	}



}
