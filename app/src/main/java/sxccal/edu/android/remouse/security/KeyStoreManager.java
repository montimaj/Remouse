package sxccal.edu.android.remouse.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * @author Abhisek Maiti
 */
public class KeyStoreManager {

	private KeyStore key_store;
	private String password;
	public KeyStoreManager(File custom_keystore, String pass_key) throws Exception {
		this.key_store = KeyStore.getInstance("pkcs12");
		if(custom_keystore.exists()) {
			key_store.load(new FileInputStream(custom_keystore), pass_key.toCharArray());
		}
		else {
			key_store.load(null, pass_key.toCharArray());
			key_store.store(new FileOutputStream(custom_keystore), pass_key.toCharArray());
		}
		this.password = pass_key;
	}

	public KeyStoreManager(String pass_key) throws Exception {
		this(new File("default_keystore"), pass_key);
	}

	public void setMasterKey(PrivateKey private_key, Certificate cert[]) throws Exception {
		KeyStore.PrivateKeyEntry privatekey_entry = new KeyStore.PrivateKeyEntry(private_key, cert);
		key_store.setEntry("Master Key",privatekey_entry,new KeyStore.PasswordProtection(password.toCharArray()));
	}
}
