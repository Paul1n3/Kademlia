/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

//import java.io.IOException;
import kademlia.JKademliaNode;
//import kademlia.message.SimpleMessage;
//import kademlia.message.SimpleReceiver;
import kademlia.node.KademliaId;

//import java.io.IOException;
import java.net.*;
//import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLServerSocket;
//import java.io.BufferedInputStream;
//import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.security.KeyManagementException;
//import java.security.KeyStore;
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
        String nom = "principal";
        String numeroCle = "0";
        String motDePasse = "gateau";
        int numeroNoeudLance = 0;
        int [][] noeuds = new int [2][4];
        String certificatsPublics [] = {"biscuit", "volant", "ciment", "arcenciel", "micro"};
        int [] ports = {0,0,0,0,0};
        InetAddress [] adresses = new InetAddress [5];
        
        
        try
        {
            //JKademliaNode kad1 = new JKademliaNode(nom, new KademliaId("1234567890123456789" + numeroNoeudLance), port);
            //System.out.println(ANSI_PURPLE + "Je suis le noeud "+ kad1.getNode().getNodeId() +" avec le port " + kad1.getPort() + ANSI_RESET);
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroCle);
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert(port, targetStream, motDePasse);
            Thread serveur = new Thread(new Boucle_Serveur(ssocket, numeroNoeudLance, ports, adresses));
            serveur.start();
            System.out.println(ANSI_PURPLE + "Je peux commencer à appeler les serveurs" + ANSI_RESET);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
       
}



