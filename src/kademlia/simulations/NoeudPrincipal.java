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
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        SSLServerSocket ssocket;
        int port = 1111;
        String nom = "principal";
        String numeroCle = "0";
        String motDePasse = "gateau";
        int numeroNoeudLance = 0;
        int [][] noeuds = new int [2][4];
        String certificatsPublics [][] = new String [3][4];
        
        try
        {
            JKademliaNode kad1 = new JKademliaNode(nom, new KademliaId("1234567890123456789" + numeroNoeudLance), port);
            System.out.println("Je suis le noeud "+ kad1.getNode().getNodeId() +" avec le port " + kad1.getPort());
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroCle);
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert((port + 2), targetStream, motDePasse);
            Thread serveur = new Thread(new Boucle_Serveur(ssocket, numeroNoeudLance));
            serveur.start();
            System.out.println("Je peux commencer à appeler les serveurs");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
       
}

