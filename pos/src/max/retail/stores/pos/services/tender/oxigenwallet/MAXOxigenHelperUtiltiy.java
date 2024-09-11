/*
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * * * * * * * * * * * Copyright (c) 2016-2017 Lifestyle India Pvt Ltd.
 * All Rights Reserved. Rev 1.2 August 9th, 2017 Vidhya Kommareddi PAYTM proxy
 * change. Added a new property to application.properties.
 * 
 * Rev 1.1 Apr 21,2017 Nadia Arora (EYLLP) posid to contain store id along with
 * register id
 * 
 * Rev 1.0 Apr 11,2017 Nadia Arora (EYLLP) Oxigen Integration Initial revision.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * * * * * * * * * *
 * 
 * package max.retail.stores.pos.services.tender.oxigenwallet;
 * 
 * 
 * import java.io.BufferedReader; import java.io.DataOutputStream; import
 * java.io.InputStream; import java.io.InputStreamReader; import
 * java.net.Authenticator; import java.net.ConnectException; import
 * java.net.HttpURLConnection; import java.net.NoRouteToHostException; import
 * java.net.SocketTimeoutException; import java.net.URL; import
 * java.net.UnknownHostException; import java.text.SimpleDateFormat; import
 * java.util.Date; import java.util.HashMap; import java.util.Map;
 * 
 * import org.apache.log4j.Logger; import org.json.simple.JSONObject;
 * 
 * import com.paytm.pg.merchant.CheckSumServiceHelper;
 * 
 * import max.retail.stores.domain.MAXOxigenResponse; import
 * max.retail.stores.pos.services.tender.MAXTenderCargo; import
 * oracle.retail.stores.foundation.tour.gate.Gateway;
 * 
 * public class MAXOxigenHelperUtiltiy implements MAXOxigenTenderConstants{ //
 * static int suffixMobileNumberCount=1; protected static final Logger logger =
 * Logger.getLogger(MAXOxigenHelperUtiltiy.class); `
 * 
 * public static MAXOxigenResponse withdrawAmount(MAXTenderCargo cargo,String
 * targetURL, String phoneNumber, String totp, String transactionId, String
 * amount, String tillId, String storeId) throws Exception { HttpURLConnection
 * connection = null; MAXOxigenResponse resp = new MAXOxigenResponse();
 * 
 * JSONObject jsonContentObj = getJsonRequestObject(cargo,phoneNumber, totp,
 * transactionId, amount);
 * 
 * logger.info("The Oxigen Withdraw Request is = " +jsonContentObj.toString());
 * //Create connection
 * 
 * URL url = new URL(targetURL);
 * 
 * connection = (HttpURLConnection)url.openConnection();
 * connection.setRequestMethod("POST");
 * 
 * String urlParameters = jsonContentObj.toString(); String CHECKSUMHASH = null;
 * 
 * //String try { String marchantKey = MAXOxigenConfig
 * .get(MAXOxigenTenderConstants.MERCHANTKEYCONFIG); CHECKSUMHASH =
 * CheckSumServiceHelper.getCheckSumServiceHelper()
 * .genrateCheckSum(marchantKey.toString().trim(),jsonContentObj.toString()); }
 * catch (Exception e) { logger.warn(e); }
 * 
 * 
 * try { //Create connection
 * 
 * connection = (HttpURLConnection)url.openConnection(); // Starts Changes for
 * proxy - Karni String useProxy = "false"; String httpProtocol =""; useProxy =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.USEPROXY);
 * logger.info("UseProxy value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.USEPROXY));
 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
 * MAXOxigenConfig.get(LSIPLWebOrderConstants.HTTPPROTOCOL); httpProtocol =
 * Gateway.getProperty("application", MAXOxigenTenderConstants.HTTPPROTOCOL,
 * ""); if( httpProtocol.equalsIgnoreCase("https")) {
 * Authenticator.setDefault(new
 * MAXProxyAuthenticator(MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER)
 * , MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD)));
 * System.setProperty("https.proxyHost",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * System.setProperty("https.proxyPort",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT)); }
 * 
 * else if( httpProtocol.equalsIgnoreCase("http")) {
 * Authenticator.setDefault(new
 * MAXProxyAuthenticator(MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER)
 * , MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD)));
 * System.setProperty("http.proxyHost",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * System.setProperty("http.proxyPort",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT)); } //Rev 1.2 end }
 * logger.info("ProxyHost value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * logger.info("ProxyPort value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT));
 * logger.info("ProxyUser value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER));
 * logger.info("ProxyPassword value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD));
 * 
 * // end Changes for proxy - Karni
 * connection.setRequestProperty(MAXOxigenTenderConstants.PHONENUMBER,
 * phoneNumber);
 * connection.setRequestProperty(MAXOxigenTenderConstants.OTP,totp);
 * 
 * connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE,
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.CONTENTTYPECONFIG));
 * connection.setRequestProperty(MAXOxigenTenderConstants.MID,
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.MERCHANTGUID));
 * connection.setRequestProperty(MAXOxigenTenderConstants.CHECKSUMHASH,
 * CHECKSUMHASH);
 * connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTLENGTH,
 * Integer.toString(urlParameters.getBytes().length));
 * connection.setUseCaches(false);
 * connection.setConnectTimeout(Integer.parseInt(MAXOxigenConfig.get(
 * MAXOxigenTenderConstants.CONNECTIONTIMEOUT)));
 * connection.setReadTimeout(Integer.parseInt(MAXOxigenConfig.get(
 * MAXOxigenTenderConstants.CONNECTIONTIMEOUT)));
 * 
 * connection.setDoOutput(true);
 * 
 * DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
 * wr.writeBytes(urlParameters); //
 * System.out.println("url parameter="+urlParameters.toString()); wr.close();
 * int responseCode = connection.getResponseCode();
 * //System.out.println("response"+responseCode);
 * resp.setResponseCode(responseCode);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.RESPONSERECEIVED);
 * resp.setRespReceivedDate(new Date()); InputStream is; if(responseCode ==
 * HttpURLConnection.HTTP_OK){ is = connection.getInputStream(); }else { is =
 * connection.getErrorStream(); }
 * 
 * BufferedReader rd = new BufferedReader(new InputStreamReader(is));
 * StringBuilder response = new StringBuilder(); // or StringBuffer if not Java
 * 5+ String line = ""; while((line = rd.readLine()) != null) {
 * response.append(line); response.append('\r'); } rd.close();
 * logger.info("The Oxigen Withdraw response string= " + response);
 * 
 * MAXOxigenResponse responseOxigen = convertOxigenResponse(response.toString(),
 * resp); return responseOxigen; } catch(ConnectException e){
 * //SocketTimeoutException
 * logger.error("\nWith  withdraw, timeout exception is " + e.getMessage() +
 * " with cause " + e.getCause()); resp = new MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
 * //resp.setOrderId(jsonContentObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; //ConnectException } catch (SocketTimeoutException e) {
 * logger.error("\nWith  withdraw, connection exception is " + e.getMessage() +
 * " with cause " + e.getCause()); resp = new MAXOxigenResponse();
 * 
 * resp.setStatusMessage(MAXOxigenTenderConstants.OXIGENTIMEOUTERROR);
 * resp.setRequestTypeA(MAXOxigenTenderConstants.TIMEOUT);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.TIMEOUT); //
 * resp.setOrderId(jsonContentObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch(NoRouteToHostException e){
 * logger.error("\nWith  withdraw, NoRouteToHostException is " + e.getMessage()
 * + " with cause " + e.getCause()); resp = new MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
 * //resp.setOrderId(jsonContentObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch(UnknownHostException e){
 * logger.error("\nWith  withdraw, UnknownHostException is " + e.getMessage() +
 * " with cause " + e.getCause()); resp = new MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE); //
 * resp.setOrderId(jsonContentObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch (Exception e) {
 * logger.error("With  withdraw, exception is " + e.getMessage()); } finally {
 * if(connection != null) { connection.disconnect(); } }
 * if(resp.getReqRespStatus() == null || resp.getReqRespStatus().equals(""))
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE); return resp; }
 * 
 * 
 * public static JSONObject getJsonRequestObject(MAXTenderCargo cargo, String
 * phoneNumber, String totp, String transactionId, String amount) throws
 * Exception { JSONObject jsonContentObj = null, jsonRequestObj = null; String
 * currencyCode = MAXOxigenTenderConstants.CURRENCY; String merchantGuid =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.MERCHANTGUID); SimpleDateFormat
 * myFormat = new SimpleDateFormat("yyyyMMddhhmmss");
 * 
 * String merchantOrderId = transactionId + myFormat.format(new
 * Date()).toString();
 * 
 * //String merchantOrderId = transactionId + myFormat.format(new
 * Date()).toString();//"07302102002808-14-17 05:14:25"; String industryType =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.INDUSTRYTYPE); Rev 1.1 changes
 * String posId = transactionId.substring(0,8); String platformName =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PLATFORMNAME); String ipAddress
 * = MAXOxigenConfig.get(MAXOxigenTenderConstants.IPADDRESS); String
 * operationType =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.OPERATIONWITHDRAW); String
 * channel = MAXOxigenConfig.get(MAXOxigenTenderConstants.CHANNEL); String
 * version = MAXOxigenConfig.get(MAXOxigenTenderConstants.VERSION);
 * 
 * jsonContentObj = new JSONObject(); jsonRequestObj = new JSONObject();
 * 
 * jsonRequestObj.put(MAXOxigenTenderConstants.TOTALAMOUNT, amount);
 * jsonRequestObj.put(MAXOxigenTenderConstants.CURRENCYCODE, currencyCode);
 * jsonRequestObj.put(MAXOxigenTenderConstants.MERCHANTGUID, merchantGuid);
 * jsonRequestObj.put(MAXOxigenTenderConstants.MERCHANTORDERID,
 * merchantOrderId); jsonRequestObj.put(MAXOxigenTenderConstants.INDUSTRYTYPE,
 * industryType); jsonRequestObj.put(MAXOxigenTenderConstants.POSID, posId); //
 * jsonRequestObj.put("comment", comment);
 * jsonContentObj.put(MAXOxigenTenderConstants.REQUEST, jsonRequestObj);
 * jsonContentObj.put(MAXOxigenTenderConstants.PLATFORMNAME, platformName);
 * jsonContentObj.put(MAXOxigenTenderConstants.IPADDRESS, ipAddress);
 * jsonContentObj.put(MAXOxigenTenderConstants.OPERATIONTYPE, operationType);
 * jsonContentObj.put(MAXOxigenTenderConstants.CHANNEL, channel);
 * jsonContentObj.put(MAXOxigenTenderConstants.VERSION,version);
 * 
 * return jsonContentObj; } public static MAXOxigenResponse
 * convertOxigenResponse(String response, MAXOxigenResponse resp) {
 * 
 * String[] tokens = response.split(","); Map<String, String> map = new
 * HashMap<>(); try { for (int index = 0; index < tokens.length-1; ) { String[]
 * keyValue = null; if(tokens[index].contains("\":{")) { keyValue =
 * tokens[index].split(":"); if(keyValue.length == 3) { keyValue[0] =
 * keyValue[1]; keyValue[1] = keyValue[2]; if(keyValue[1] != null &&
 * keyValue[1].startsWith("\"")) keyValue[1] = keyValue[1].substring(1);
 * if(keyValue[1] != null && keyValue[1].endsWith("\"")) keyValue[1] =
 * keyValue[1].substring(0, keyValue[1].length()-1); } ++index; } else {
 * keyValue = tokens[index].split("\":"); if(keyValue != null && keyValue.length
 * >= 2) { if(keyValue[1] != null && keyValue[1].startsWith("\"")) keyValue[1] =
 * keyValue[1].substring(1); if(keyValue[1] != null &&
 * keyValue[1].endsWith("\"")) keyValue[1] = keyValue[1].substring(0,
 * keyValue[1].length()-1); } ++index; }
 * map.put(keyValue[0].substring(keyValue[0].indexOf('\"') + 1,
 * keyValue[0].length()), keyValue[1]); } } catch(Exception e) {
 * logger.error("Error in converting paytm response : " + e.getMessage()); }
 * resp.setOrderId(map.get("orderId")); resp.setStatus(map.get("status"));
 * resp.setStatusCode(map.get("statusCode"));
 * resp.setStatusMessage(map.get("statusMessage"));
 * resp.setWalletTxnId(map.get("walletSystemTxnId"));
 * resp.setOxigenResponse(response); resp.setDataException(Boolean.FALSE);
 * if(map.get("refundTxnGuid\"") != null) {
 * resp.setWalletTxnId(map.get("refundTxnGuid\"")); } return resp; }
 * 
 * public static MAXOxigenResponse reverseAmount(String orderId, String
 * targetURL, String phoneNumber, String amount, String orgTxnId) throws
 * Exception { HttpURLConnection connection = null; MAXOxigenResponse resp = new
 * MAXOxigenResponse(); JSONObject jsonRequestObj =
 * getJsonReverseRequestObject(orderId, phoneNumber, amount, orgTxnId);
 * 
 * logger.info("The Oxigen Reversal Request is = " +jsonRequestObj.toString());
 * 
 * targetURL = Gateway.getProperty("application", "OxigenReversalURL", "");
 * //Create connection URL url = new URL(targetURL);
 * 
 * connection = (HttpURLConnection)url.openConnection();
 * connection.setRequestMethod("POST");
 * 
 * String urlParameters=jsonRequestObj.toString(); String
 * marchantKey=MAXOxigenConfig.get(MAXOxigenTenderConstants.MERCHANTKEYCONFIG);
 * String CHECKSUMHASH =
 * CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(marchantKey.
 * toString().trim(), jsonRequestObj.toString());
 * 
 * logger.info("The Oxigen Reversal checksum is " + CHECKSUMHASH); try {
 * //Create connection
 * 
 * connection = (HttpURLConnection)url.openConnection();
 * 
 * String useProxy = "true"; String httpProtocol =""; useProxy =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.USEPROXY);
 * logger.info("UseProxy value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.USEPROXY));
 * if(useProxy.equalsIgnoreCase("true")) { //Rev 1.2 start //httpProtocol =
 * MAXOxigenConfig.get(LSIPLWebOrderConstants.HTTPPROTOCOL); httpProtocol =
 * Gateway.getProperty("application", MAXOxigenTenderConstants.HTTPPROTOCOL,
 * ""); if( httpProtocol.equalsIgnoreCase("https")) {
 * Authenticator.setDefault(new
 * MAXProxyAuthenticator(MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER)
 * , MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD)));
 * System.setProperty("https.proxyHost",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * System.setProperty("https.proxyPort",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT)); }
 * 
 * else if( httpProtocol.equalsIgnoreCase("http")) {
 * Authenticator.setDefault(new
 * MAXProxyAuthenticator(MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER)
 * , MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD)));
 * System.setProperty("http.proxyHost",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * System.setProperty("http.proxyPort",
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT)); } //Rev 1.2 end }
 * logger.info("ProxyHost value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYHOST));
 * logger.info("ProxyPort value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPORT));
 * logger.info("ProxyUser value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYUSER));
 * logger.info("ProxyPassword value " +
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PROXYPASSWORD));
 * 
 * connection.setRequestProperty(MAXOxigenTenderConstants.PHONENUMBER,
 * phoneNumber);
 * 
 * 
 * connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE,
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.CONTENTTYPECONFIG));
 * connection.setRequestProperty(MAXOxigenTenderConstants.MID,
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.MERCHANTGUID));
 * connection.setRequestProperty(MAXOxigenTenderConstants.CHECKSUMHASH,
 * CHECKSUMHASH);
 * connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTLENGTH,
 * Integer.toString(urlParameters.getBytes().length));
 * connection.setUseCaches(false);
 * connection.setConnectTimeout(Integer.parseInt(MAXOxigenConfig.get(
 * MAXOxigenTenderConstants.CONNECTIONTIMEOUT)));
 * connection.setReadTimeout(Integer.parseInt(MAXOxigenConfig.get(
 * MAXOxigenTenderConstants.CONNECTIONTIMEOUT))); connection.setDoOutput(true);
 * 
 * DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
 * wr.writeBytes(urlParameters); wr.close(); int responseCode =
 * connection.getResponseCode();
 * 
 * resp.setReqRespStatus(MAXOxigenTenderConstants.RESPONSERECEIVED);
 * resp.setRespReceivedDate(new Date()); InputStream is; if(responseCode ==
 * HttpURLConnection.HTTP_OK){ is = connection.getInputStream(); }else { is =
 * connection.getErrorStream(); }
 * 
 * resp.setResponseCode(responseCode); BufferedReader rd = new
 * BufferedReader(new InputStreamReader(is)); StringBuilder response = new
 * StringBuilder(); // or StringBuffer if not Java 5+ String line = "";
 * while((line = rd.readLine()) != null) { response.append(line);
 * response.append('\r'); } rd.close();
 * logger.info("The Oxigen Reversal Response is= " + response);
 * 
 * MAXOxigenResponse responseOxigen = convertOxigenResponse(response.toString(),
 * resp); logger.info("The Oxigen Reversal cenverted Response is= " +
 * responseOxigen); return responseOxigen; } catch(SocketTimeoutException e){
 * logger.error("\nWith  Oxigen reversal, timeout exception is " +
 * e.getMessage() + " with cause " + e.getCause()); resp = new
 * MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.OXIGENTIMEOUTERROR);
 * resp.setRequestTypeA(MAXOxigenTenderConstants.TIMEOUT);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.TIMEOUT); //
 * resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch (ConnectException e) {
 * logger.error("\nWith  paytm reversal, connection exception is " +
 * e.getMessage() + " with cause " + e.getCause()); resp = new
 * MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE); //
 * resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch(NoRouteToHostException e){
 * logger.error("\nWith  paytm reversal, NoRouteToHostException is " +
 * e.getMessage() + " with cause " + e.getCause()); resp = new
 * MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE); //
 * resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch(UnknownHostException e){
 * logger.error("\nWith  paytm reversal, UnknownHostException is " +
 * e.getMessage() + " with cause " + e.getCause()); resp = new
 * MAXOxigenResponse();
 * resp.setStatusMessage(MAXOxigenTenderConstants.NETWORKERROR);
 * resp.setReqRespStatus(MAXOxigenTenderConstants.
 * RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
 * //resp.setOrderId(jsonRequestObj.getJSONObject("request").getString(
 * MAXOxigenTenderConstants.MERCHANTORDERID)); logger.error(e.getMessage());
 * return resp; } catch (Exception e) {
 * logger.error(" error while paytm reversal " + e.getMessage());
 * logger.error(e.getMessage()); } finally { if(connection != null) {
 * connection.disconnect(); } }
 * logger.info("The Oxigen Reversal after all the catches "); return resp; }
 * 
 * 
 * public static JSONObject getJsonReverseRequestObject(String orderId, String
 * phoneNumber, String amount, String orgTrnId) throws Exception { JSONObject
 * jsonContentObj = null, jsonRequestObj = null;
 * 
 * String currencyCode = MAXOxigenTenderConstants.CURRENCY; String merchantGuid
 * = MAXOxigenConfig.get(MAXOxigenTenderConstants.MERCHANTGUID);
 * //SimpleDateFormat myFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
 * //String merchantOrderId = transactionId + myFormat.format(new
 * Date()).toString();//"07302102002808-14-17 05:14:25"; String merchantOrderId
 * = orderId.toString().trim();
 * 
 * String platformName =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.PLATFORMNAME); String ipAddress
 * = MAXOxigenConfig.get(MAXOxigenTenderConstants.IPADDRESS); String
 * operationType =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.OPERATIONREFUND); String channel
 * = MAXOxigenConfig.get(MAXOxigenTenderConstants.CHANNEL); String version =
 * MAXOxigenConfig.get(MAXOxigenTenderConstants.VERSION);
 * 
 * jsonContentObj = new JSONObject(); jsonRequestObj = new JSONObject();
 * 
 * jsonRequestObj.put(MAXOxigenTenderConstants.TXNGUID, orgTrnId);
 * jsonRequestObj.put(MAXOxigenTenderConstants.AMOUNT, amount);
 * jsonRequestObj.put(MAXOxigenTenderConstants.CURRENCYCODE, currencyCode);
 * jsonRequestObj.put(MAXOxigenTenderConstants.MERCHANTGUID, merchantGuid);
 * jsonRequestObj.put(MAXOxigenTenderConstants.MERCHANTORDERID,
 * merchantOrderId); jsonContentObj.put(MAXOxigenTenderConstants.REQUEST,
 * jsonRequestObj);
 * 
 * jsonContentObj.put(MAXOxigenTenderConstants.IPADDRESS, ipAddress);
 * jsonContentObj.put(MAXOxigenTenderConstants.PLATFORMNAME, platformName);
 * jsonContentObj.put(MAXOxigenTenderConstants.OPERATIONTYPE, operationType);
 * jsonContentObj.put(MAXOxigenTenderConstants.CHANNEL, channel);
 * jsonContentObj.put(MAXOxigenTenderConstants.VERSION,version);
 * 
 * return jsonContentObj; } }
 * 
 */