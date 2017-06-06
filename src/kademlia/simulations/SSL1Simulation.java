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
import javax.net.ssl.SSLServerSocket;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


/**
 *
 * @author Pauline
 */
public class SSL1Simulation {
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        SSLServerSocket ssocket;
        try
        {
            JKademliaNode kad1 = new JKademliaNode("Farine", new KademliaId("12345678901234567890"), 7574);
            //JKademliaNode kad2 = new JKademliaNode("Sucre", new KademliaId("12345678901234567891"), 7572);
            
            File initialFile = new File("/Users/Pauline/Desktop/socketJava/CLE");
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert(2009, targetStream, "chocolat");
            Thread t = new Thread(new Accepter_clients(ssocket));
            t.start();
            System.out.println("Mes employeurs sont prêts !");

            //kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class Accepter_clients implements Runnable {

  private ServerSocket socketserver;
  private Socket socket;
  private int nbrclient = 1;

  public Accepter_clients(ServerSocket s){
    socketserver = s;
  }
        
  public void run() {

    try {
      while(true){
        socket = socketserver.accept(); // Un client se connecte on l'accepte
        System.out.println("Le client numéro "+nbrclient+ " est connecté !");
        nbrclient++;
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
