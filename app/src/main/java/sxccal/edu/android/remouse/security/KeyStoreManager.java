package sxccal.edu.android.remouse.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import static sxccal.edu.android.remouse.MainActivity.remouseDir;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */
class KeyStoreManager {

    private KeyStore mKeyStore;
    private static final String KEY_STORE_TYPE = "pkcs12";
    private static final String KEY_STORE_ALIAS = "Remouse KeyStore";
    private static final String KEY_STORE_PASSWORD = "foo";
    private static final String KEY_STORE_NAME = remouseDir + "/remouse_keystore";

    KeyStoreManager() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        mKeyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        mKeyStore.load(null, KEY_STORE_PASSWORD.toCharArray());
    }

    static boolean keyStoreExists() {
        return new File(KEY_STORE_NAME).exists();
    }

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
}
