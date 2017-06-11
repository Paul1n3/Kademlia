/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Pauline
 */
public class Init_Reseau {
        
    public static void initialisation(String [] infos, String [] certificatsPublics) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        int compteur = 0;
        SSLSocket socket;
        BufferedReader reader;
        String motDePasse = "gateau";
        int numeroNoeud;
        int numeroPort;
        InetAddress addr;
        
        
        try
        {
            while(compteur < infos.length){
                numeroNoeud = Integer.parseInt(infos[compteur]);
                System.out.println("Je veux communiquer avec le noeud " + numeroNoeud);
                compteur++;
                addr = InetAddress.getByName(infos[compteur]);
                System.out.println("Son adresse IP est " + addr);
                compteur++;
                numeroPort = Integer.parseInt(infos[compteur]) + 2;
                System.out.println("Son numéro de port est " + numeroPort);
                compteur++;
                motDePasse = certificatsPublics[numeroNoeud];
                System.out.println("Le mot de passe est " + motDePasse);
                
                
                
                File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numeroNoeud);
                InputStream targetStream = new FileInputStream(initialFile);
                System.out.println("Tentative de connection");
                socket = SSLSocketKeystoreFactory.getSocketWithCert(addr, numeroPort, targetStream, motDePasse);
                System.out.println("Connection établie");
                PrintWriter writer;
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("PING");
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(reader.readLine());
                socket.close();
            }
        }
            
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
