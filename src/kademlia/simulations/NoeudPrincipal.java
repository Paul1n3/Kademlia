/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

import kademlia.JKademliaNode;
import kademlia.node.KademliaId;

import java.net.*;
import javax.net.ssl.SSLServerSocket;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


/**
 *
 * @author Pauline
 */
public class NoeudPrincipal {
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";
    
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        
        SSLServerSocket ssocket;
        int port = 1111;
        
        // Mot de passe de la clé privée
        String motDePasse = "gateau";
        int numeroNoeudLance = 0;
        String certificatsPublics [] = {"biscuit", "volant", "ciment", "arcenciel", "micro"};
        
        try
        {
            // Ouverture de la socket sécurisée serveur
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroNoeudLance);
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert(port, targetStream, motDePasse);
            
            Thread serveur = new Thread(new Boucle_Serveur(ssocket, numeroNoeudLance));
            serveur.start();
            System.out.println(ANSI_PURPLE + "Je peux commencer à appeler les serveurs" + ANSI_RESET);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
       
}



