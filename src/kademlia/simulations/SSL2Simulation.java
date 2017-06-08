/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

import java.io.IOException;
import kademlia.JKademliaNode;
import kademlia.message.SimpleMessage;
import kademlia.message.SimpleReceiver;
import kademlia.node.KademliaId;

import java.io.IOException;
import java.net.*;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 *
 * @author Pauline
 */
public class SSL2Simulation {
    public static void main(String [] args)throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        
        SSLSocket socket;
        BufferedReader reader = null;
        
        //int port = 1234;
        String nom = "Client";
        //String numeroCertificat = "0";
        String motDePasse = "biscuit";    
        try
        {
            JKademliaNode kadClient = new JKademliaNode(nom, new KademliaId("12345678901234567890"), 7545);
            //JKademliaNode kad2 = new JKademliaNode("Sucre", new KademliaId("12345678901234567891"), 7513);
            
            //kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
            
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat0");
            InputStream targetStream = new FileInputStream(initialFile);
            System.out.println("Tentative de connection");
            socket = SSLSocketKeystoreFactory.getSocketWithCert(InetAddress.getLocalHost(), 1236, targetStream, motDePasse);
            System.out.println("Connection établie");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(reader.readLine());
            socket.close();
        }
            
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
}
    
