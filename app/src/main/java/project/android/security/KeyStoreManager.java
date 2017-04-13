package project.android.security;

import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import static project.android.MainActivity.sRemouseDir;
import static project.android.MainActivity.sSharedPrefs;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */
class KeyStoreManager {

    private KeyStore mKeyStore;

    private static final String KEY_STORE_TYPE = "pkcs12";
    private static final String KEY_STORE_ALIAS = "Remouse KeyStore";
    private static final String KEY_STORE_PASSWORD = generateKeystorePassword();
    private static final String KEY_STORE_NAME = sRemouseDir + "/remouse_keystore";

    KeyStoreManager() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        mKeyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        mKeyStore.load(null, KEY_STORE_PASSWORD.toCharArray());
    }

    static boolean keyStoreExists() { return new File(KEY_STORE_NAME).exists(); }

    void setMasterKey(PrivateKey privateKey, Certificate ...cert) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, cert);
        mKeyStore.setEntry(KEY_STORE_ALIAS, privateKeyEntry,new KeyStore.PasswordProtection(KEY_STORE_PASSWORD.toCharArray()));
        mKeyStore.store(new FileOutputStream(KEY_STORE_NAME), KEY_STORE_PASSWORD.toCharArray());
    }

    static KeyPair getKSKeyPair() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {
        FileInputStream fis = new FileInputStream(KEY_STORE_NAME);

        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        keyStore.load(fis, KEY_STORE_PASSWORD.toCharArray());

        Key key = keyStore.getKey(KEY_STORE_ALIAS, KEY_STORE_PASSWORD.toCharArray());
        if(key instanceof PrivateKey) {
            Certificate certificate = keyStore.getCertificate(KEY_STORE_ALIAS);
            PublicKey publicKey = certificate.getPublicKey();
            return new KeyPair(publicKey, (PrivateKey) key);
        }
        return null;
    }

    private static String generateKeystorePassword() {
        String password = sSharedPrefs.getString("Password", null);
        if(password == null) {
            SecureRandom random = new SecureRandom();
            StringBuilder stringBuilder = new StringBuilder(new BigInteger(36, 0, random).toString(Character.MAX_RADIX));
            while (stringBuilder.length() > 6) {
                stringBuilder.deleteCharAt(random.nextInt(stringBuilder.length()));
            }
            for (int i = 0; i < stringBuilder.length(); i++) {
                char ch = stringBuilder.charAt(i);
                if (Character.isLetter(ch) && Character.isLowerCase(ch) && random.nextFloat() < 0.5) {
                    stringBuilder.setCharAt(i, Character.toUpperCase(ch));
                }
            }
            SharedPreferences.Editor editor = sSharedPrefs.edit();
            password = stringBuilder.toString();
            editor.putString("Password", password);
            editor.apply();
        }
        System.out.println("password: " + password);
        return password;
    }
}