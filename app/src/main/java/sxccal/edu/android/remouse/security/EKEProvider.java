package sxccal.edu.android.remouse.security;

import org.apache.commons.codec.binary.Base64;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */


public class EKEProvider {

	private String mPairingKey;
	private KeyPair mKeyPair;
	private SecretKey mAccessKey;
	private byte[] mIV;

	public EKEProvider() {
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
		SecureRandom random = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder(new BigInteger(30, random).toString(32));
		for(int i=0;i<stringBuilder.length();i++) {
			char ch = stringBuilder.charAt(i);
			if(Character.isLetter(ch) && Character.isLowerCase(ch) && random.nextFloat()<0.5) {
				stringBuilder.setCharAt(i, Character.toUpperCase(ch));
			}
		}
		this.mPairingKey = stringBuilder.toString();

		MessageDigest msgDigest;
		try {
			msgDigest = MessageDigest.getInstance("SHA-256", "SC");
			this.mIV = msgDigest.digest(mPairingKey.getBytes());
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException nsae) {
			nsae.printStackTrace();
		}

	}

	public String getPairingKey() { return mPairingKey; }

	public EKEProvider(String k) {
		this.mPairingKey = k;
		MessageDigest msgDigest;
		try {
			msgDigest = MessageDigest.getInstance("SHA-256", "SC");
			this.mIV = msgDigest.digest(mPairingKey.getBytes());
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException nsae) {
			nsae.printStackTrace();
		}
	}

	public void generateMasterKeys() {
		this.mKeyPair = generateECKeys();
	}

	public void generateAccessKey(PublicKey XPubKey) {
		this.mAccessKey = generateSharedSecret(mKeyPair.getPrivate(), XPubKey);
	}

	public byte[] getBase64EncodedPubKey() {
		KeyPair keyPair = this.generateECKeys();
		if (keyPair != null)    return Base64.encodeBase64(keyPair.getPublic().getEncoded());
		return "".getBytes();
	}

	/*public static void main(String[] args) {

		EKEProvider ekeProvider = new EKEProvider();
		String plainText = "Look mah, I'm a message!";
		System.out.println("Original plaintext message: " + plainText);

		// Initialize two key pairs
		KeyPair keyPair = ekeProvider.generateECKeys();
		KeyPair keyPairB = ekeProvider.generateECKeys();
		// Create two AES secret keys to encrypt/decrypt the message
		SecretKey secretKeyA = ekeProvider.generateSharedSecret(keyPairA.getPrivate(), keyPairB.getPublic());
		SecretKey secretKeyB = ekeProvider.generateSharedSecret(keyPairB.getPrivate(), keyPairA.getPublic());

		// Encrypt the message using 'secretKeyA'
		String cipherText = ekeProvider.encryptString(secretKeyA, plainText);
		System.out.println("Encrypted cipher text: " + cipherText);

		// Decrypt the message using 'secretKeyB'
		String decryptedPlainText = ekeProvider.decryptString(secretKeyB, cipherText);
		System.out.println("Decrypted cipher text: " + decryptedPlainText);
	}*/

	private KeyPair generateECKeys() {
		try {
			ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("brainpoolp256r1");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "SC");
			keyPairGenerator.initialize(parameterSpec);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}

	private SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) {
		try {
			KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "SC");
			keyAgreement.init(privateKey);
			keyAgreement.doPhase(publicKey, true);
			return keyAgreement.generateSecret("AES");
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String encryptString(SecretKey key, String plainText) {
		try {
			IvParameterSpec ivSpec = new IvParameterSpec(mIV);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SC");
			byte[] plainTextBytes = plainText.getBytes("UTF-8");
			byte[] cipherText;

			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			cipherText = new byte[cipher.getOutputSize(plainTextBytes.length)];
			int encryptLength = cipher.update(plainTextBytes, 0, plainTextBytes.length, cipherText, 0);
			encryptLength += cipher.doFinal(cipherText, encryptLength);
			return Base64.encodeBase64String(cipherText);
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException
				| UnsupportedEncodingException | ShortBufferException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String decryptString(SecretKey key, String cipherText) {
		try {
			Key decryptionKey = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
			IvParameterSpec ivSpec = new IvParameterSpec(mIV);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SC");
			byte[] cipherTextBytes = Base64.decodeBase64(cipherText);
			byte[] plainText;

			cipher.init(Cipher.DECRYPT_MODE, decryptionKey, ivSpec);
			plainText = new byte[cipher.getOutputSize(cipherTextBytes.length)];
			int decryptLength = cipher.update(cipherTextBytes, 0, cipherTextBytes.length, plainText, 0);
			decryptLength += cipher.doFinal(plainText, decryptLength);

			return new String(plainText, "UTF-8");
		} catch (NoSuchAlgorithmException | NoSuchProviderException
				| NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException
				| IllegalBlockSizeException | BadPaddingException
				| ShortBufferException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Certificate genSelfSignedCert(PublicKey public_key, PrivateKey private_key) throws Exception {
		X500Principal issuer = new X500Principal("CN=SAS");
		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
		Date notbefore = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		Date notafter = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);
		X500Principal subject = new X500Principal("SUB=Self Signed");
		X509v3CertificateBuilder cert_gen = new JcaX509v3CertificateBuilder(issuer, serial, notbefore, notafter, subject, public_key);
		X509CertificateHolder cert_holder = cert_gen.build(new JcaContentSignerBuilder("SHA5121WithRSA").setProvider("SC").build(private_key));
		return new JcaX509CertificateConverter().setProvider("SC").getCertificate(cert_holder);
	}
}
