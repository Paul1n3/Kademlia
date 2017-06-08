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
public class SSL1Simulation {
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        SSLServerSocket ssocket;
        int port = Integer.parseInt(args[0]);
        String nom = args[1];
        String numeroCle = args[2];
        String motDePasse = args[3];
        
        try
        {
            JKademliaNode kad1 = new JKademliaNode(nom, new KademliaId("12345678901234567890"), port);
            System.out.println("Je suis le noeud avec le port " + kad1.getPort());
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroCle);
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert((port + 2), targetStream, motDePasse);
            //JKademliaNode kad2 = new JKademliaNode("Sucre", new KademliaId("12345678901234567891"), 7572);
            //kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
            Thread serveur = new Thread(new Boucle_Serveur(ssocket));
            serveur.start();
            System.out.println("Je peux commencer à appeler les serveurs");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}

class Accepter_clients implements Runnable {

  private Socket socket;
 

  public Accepter_clients(Socket s){
    socket = s;
  }
        
  @Override
  public void run() {

    try {
        PrintWriter writer;
        writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("Message1");
        System.out.println("Un nouveau client s'est connecté !");
        
        socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

class Boucle_Serveur implements Runnable {
 
    SSLServerSocket ssocket;
    public Boucle_Serveur(SSLServerSocket s){
        ssocket = s;
    }

    @Override
    public void run() {
      Socket socket;
      try
      {
          while(true){
              System.out.println("Je suis en attente de clients.");
              socket= ssocket.accept();
              System.out.println("Connexion cliente reçue.");
              Thread t = new Thread(new Accepter_clients(socket));
              t.start();
          }
      }catch (IOException e)
      {
          e.printStackTrace();
      }
    }
}

