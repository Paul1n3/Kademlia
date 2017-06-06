/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

/*
 *  Copyright (C) 2015 Gabriel POTTER (gpotter2)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/**
 * Util class to create SSLServerSocket using a KeyStore certificate to connect a server
 * 
 * @author gpotter2
 *
 */
public class SSLServerSocketKeystoreFactory {
	
	private static String instance;
	/**
	 * CONFIGURATION SECTION
	 */
	static {
		instance = "JKS"/* TODO REPLACE WITH BKS IF USING IT*/;
		/*
		 * Several Notes: 
		 * - Android only works with BKS, so you need to use only BKS certs files
		 * - As before Android 15, BKS-v1 was used, you need to convert BKS in BKS-v1 to use it in Android 15-; BUT as Android 23+ doesn't support BKS-v1
		 * and as BKS-v1 is deprecated, you need to have both of the certs and use them in fuction of the version
		 * - Java doesn't support BKS without library
		 * - A BKS format client can be connected a JKS format server
		 */
	}
	
	/**
	 * 
	 * A SSL algorithms types chooser enum
	 * 
	 * @author gpotter2
	 *
	 */
	public static enum ServerSecureType {
		@Deprecated
		SSL("SSL"),
		@Deprecated
		SSLv2("SSLv2"),
		SSLv3("SSLv3"),
		@Deprecated
		TLS("TLS"),
		@Deprecated
		TLSv1("TLSv1"),
		TLSv1_1("TLSv1.1"),
		TLSv1_2("TLSv1.2");
		
		private String type;
		
		private ServerSecureType(String type){
			this.type = type;
		}
		public String getType(){
			return type;
		}
	}
	
	public static SSLServerSocket getServerSocketWithCert(int port, InputStream pathToCert, String passwordFromCert) throws IOException,
                                    KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        TrustManager[] tmm = new TrustManager[1];
        KeyManager[] kmm = new KeyManager[1];
        //On charge le lecteur de Keystore en fonction du format
        //ATTENTION
        //POUR LES SERVEURS android, remplacez le JKS par BKS, si c'est juste le client qui est android, laissez JKS
        KeyStore ks  = KeyStore.getInstance("JKS");
        //On charge le Keystore avec sont stream et son mot de passe
        ks.load(pathToCert, passwordFromCert.toCharArray());
        //On lance les gestionnaires de clés et de vérification des clients
        tmm[0]=tm(ks);
        kmm[0]=km(ks, passwordFromCert);
        //On démarre le contexte, autrement dit le langage utilisé pour crypter les données
        //ATTENTION
        //Ici, on peut remplacer SSL par TLSv1.2 , mais il faudra le faire aussi bien dans le client que le serveur
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(kmm, tmm, null);
        //On lance la serversocket sur le port indiqué, avec le contexte fourni
        SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) ctx.getServerSocketFactory();
        return (SSLServerSocket) socketFactory.createServerSocket(port);
    }
	private static X509TrustManager tm(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustMgrFactory.init(keystore);
        //on prend tous les managers
        TrustManager trustManagers[] = trustMgrFactory.getTrustManagers();
        for (int i = 0; i < trustManagers.length; i++) {
            if (trustManagers[i] instanceof X509TrustManager) {
                //on renvoie juste celui que l'on va utiliser
                return (X509TrustManager) trustManagers[i];
            }
        }
        return null;
    };
    /**
     * Ca c'est une fonction utilitaire permettant de récupérer le "gestionnaire de mot de passes des clés" (en gros)
     * 
     * Pas très intéressante...
     */
    private static X509KeyManager km(KeyStore keystore, String password) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyMgrFactory.init(keystore, password.toCharArray());
        //on prend tous les managers
        KeyManager keyManagers[] = keyMgrFactory.getKeyManagers();
        for (int i = 0; i < keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509KeyManager) {
                //on renvoie juste celui que l'on va utiliser
                return (X509KeyManager) keyManagers[i];
            }
        }
        return null;
    };
}
