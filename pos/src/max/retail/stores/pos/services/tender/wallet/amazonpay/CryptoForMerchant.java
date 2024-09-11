/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.wallet.amazonpay;

/**
@author Purushotham Reddy Sirison
**/


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import lombok.NonNull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CryptoForMerchant {

	private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
	private static final String AES = "AES";
	private static final String RSA = "RSA";
	private static final String RSA_WITH_NO_PADDING = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
	private static final SecureRandom RANDOM;
	// private static final Encoder urlEncoder;
	private static final ObjectMapper objectMapper;
	/**
	 * * Reuse cryptographic ciphers for performance, one per thread. * Note:
	 * Ciphers by themselves are not thread safe.
	 */
	/*
	 * private static final Cipher AEC_GCM_THREAD_CIPHER; private static final
	 * Cipher RSA_THREAD_CIPHER;
	 */

	static {
		try {
			objectMapper = new ObjectMapper();
			// urlEncoder = Base64.getUrlEncoder();

			RANDOM = new SecureRandom();
			Security.addProvider(new BouncyCastleProvider());
			/*
			 * AEC_GCM_THREAD_CIPHER = Cipher.getInstance(AES_GCM_NO_PADDING,
			 * BouncyCastleProvider.PROVIDER_NAME);
			 * 
			 * 
			 * RSA_THREAD_CIPHER = Cipher.getInstance(RSA_WITH_NO_PADDING,
			 * BouncyCastleProvider.PROVIDER_NAME);
			 */

		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Map<String, String> encrypt(Map<String, Object> map)
			throws JsonProcessingException, GeneralSecurityException {
		byte[] payload = objectMapper.writeValueAsBytes(map);
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomKey = new byte[16];
		secureRandom.nextBytes(randomKey);
		return encrypt(payload, randomKey);
	}

	/*
	 *  * Encrypt the byte array using the secret key and algorithm: {@value
	 * #AES_GCM_NO_PADDING}, * allowing for prefix data to be written directly
	 * to output later. * Output will contain a new randomly-generated
	 * initialization vector and then the cipher/encrypted bytes. * @param
	 * payload plain bytes to encrypt. * @param key the secret key byte array of
	 * length 16, 24 or 32. * @return encrypted byte array that includes a
	 * random initialization vector up front.
	 */

	public static Map<String, String> encrypt(@NonNull final byte[] payload,
			@NonNull final byte[] key) throws GeneralSecurityException {
		String payloadProperty = "";
		String ivProperty = "";
		String keyProperty = "";
		SecretKeySpec codingKey = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING,
				BouncyCastleProvider.PROVIDER_NAME);
		byte[] iv = new byte[cipher.getBlockSize()];
		RANDOM.nextBytes(iv);
		cipher.init(Cipher.ENCRYPT_MODE, codingKey, new IvParameterSpec(iv));
		final byte[] encryptedPayload = cipher.doFinal(payload);
		byte[] encryptMerchantKey = encryptMerchantKey(key);
		try {
			payloadProperty = encodeToUrlString(encryptedPayload);
			ivProperty = encodeToUrlString(iv);
			keyProperty = encodeToUrlString(encryptMerchantKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("payload", payloadProperty);
		resultMap.put("iv", ivProperty);
		resultMap.put("key", keyProperty);
		return resultMap;

	}

	private static byte[] encryptMerchantKey(final byte[] key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, BadPaddingException,
			IllegalBlockSizeException, NoSuchProviderException,
			NoSuchPaddingException {
		KeyFactory keyFact = KeyFactory.getInstance(RSA);
		String preSharedEncodedKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq92yAzXaCQbGIid0mMBfulkGK8Hqv"
				+ "AardDowtgbfGUZ+hIx6lhYKFMrluTr7bIlQ4qgJY85c9adkZSxHtr/DhTV/ch5CCHDET3YC/DaFTKDp5t2uHKQAIb2Rl/"
				+ "73HQOd/pgImTiaLHPBr/gyz4iztYmlJQIm0vVuPktIANDGpK8qhizdztA3as1bLtILQZ5VtOjNn/"
				+ "xl1HQ+JDtBhUVr13BuJPosecQz6ouhEtR+5i/grg6sUzayqPD1dY6AGRLR9ao/6DCeHT5arSYjlkx6BECuKoiARo7It"
				+ "DfLameXJ1gLd8lkMzArIG275jbxAiPd4OchHEfcqBADYB51FYDTwQIDAQAB";
		KeySpec spec = new X509EncodedKeySpec(org.bouncycastle.util.encoders.Base64.decode(preSharedEncodedKey));
		PublicKey publicKey = keyFact.generatePublic(spec);
		Cipher cipher = Cipher.getInstance(RSA_WITH_NO_PADDING,BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(key);

	}

	/**
	 * * Encodes to base-64 URL UTF-8 Sting.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private static String encodeToUrlString(byte[] array)
			throws UnsupportedEncodingException {
		
		String payload = DatatypeConverter.printBase64Binary(array).replace("+", "-") .replace("/", "_");

		return payload;
	}
}
