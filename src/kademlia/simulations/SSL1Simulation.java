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
import java.io.PrintWriter;

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
        //SSLServerSocket ssocket2;
        int port = 4789;
   
        
        try
        {
            //JKademliaNode kad1 = new JKademliaNode("Farine", new KademliaId("12345678901234567890"), 7545);
            //JKademliaNode kad2 = new JKademliaNode("Sucre", new KademliaId("12345678901234567891"), 7572);
            //while(true){
               
                File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE");
                InputStream targetStream = new FileInputStream(initialFile);
                ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert(1234, targetStream, "chocolat");
                Thread t = new Thread(new Accepter_clients(ssocket));
                t.start();
                System.out.println("Le premier client a été traité !");

                //File deuxiemeFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE");
                //InputStream targetStream2 = new FileInputStream(deuxiemeFile);
                //ssocket2 = SSLServerSocketKeystoreFactory.getServerSocketWithCert(2022, targetStream2, "chocolat");

                //Thread t2 = new Thread(new Accepter_clients(ssocket2));
                //t2.start();
                //System.out.println("Le deuxième client a été traité");

                //kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
            }
        //}
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

class Accepter_clients implements Runnable {

  private SSLServerSocket socketserver;
  private Socket socket;
 

  public Accepter_clients(SSLServerSocket s){
    socketserver = s;
  }
        
  @Override
  public void run() {

    try {
        PrintWriter writer;
        socket = socketserver.accept(); // Un client se connecte on l'accepte
        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("Message1");
        System.out.println("Un nouveau client s'est connecté !");
        
        socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
