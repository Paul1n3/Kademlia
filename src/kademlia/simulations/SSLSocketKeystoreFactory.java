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
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;

/**
 * Util class to create SSLSocket using a KeyStore certificate to connect a server
 * 
 * @author gpotter2
 *
 */
public class SSLSocketKeystoreFactory {
	
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
	public static enum SecureType {
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
		
		private SecureType(String type){
			this.type = type;
		}
		public String getType(){
			return type;
		}
	}
	
	/**
	 * Ca c'est une fonction utilitaire permettant de récupérer le "vérifieur de certificat", on prend uniquement celui correspondant au certificat pour minimiser le temps de chargement à la connexion
	 * 
	 * Pas très intéressante...
	 */
	private static X509TrustManager[] tm(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory trustMgrFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustMgrFactory.init(keystore);
		//on prend tous les managers
		TrustManager trustManagers[] = trustMgrFactory.getTrustManagers();
		for (TrustManager trustManager : trustManagers) {
			if (trustManager instanceof X509TrustManager) {
				X509TrustManager[] tr = new X509TrustManager[1];
				//on renvoie juste celui que l'on va utiliser
				tr[0] = (X509TrustManager) trustManager;
				return tr;
			}
		}
		return null;
	}
	
	//Le vrai du code 

    public static SSLSocket getSocketWithCert(InetAddress ip, int port, InputStream pathToCert, String passwordFromCert) throws IOException,
									KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		X509TrustManager[] tmm;
		//ATTENTION
		//Android, remplacez JKS par BKS :)
		//On charge le lecteur de Keystore en fonction du format
		KeyStore ks  = KeyStore.getInstance("JKS");
		//On charge le Keystore avec sont stream et son mot de passe
		ks.load(pathToCert, passwordFromCert.toCharArray());
		//On démarre le gestionnaire de validation des clés
		tmm=tm(ks);
		//On démarre le contexte, autrement dit le langage utilisé pour crypter les données
		//ATTENTION
		//On peut replacer SSL par TLSv1.2
		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
		ctx.init(null, tmm, null);
		//On créee enfin la socket en utilisant une classe de création SSLSocketFactory, vers l'adresse et le port indiqué
		SSLSocketFactory SocketFactory = ctx.getSocketFactory();
		SSLSocket socket = (SSLSocket) SocketFactory.createSocket();
		socket.connect(new InetSocketAddress(ip, port), 5000);
		return socket;
	}

	
}
