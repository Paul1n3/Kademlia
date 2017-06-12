/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

//import java.io.IOException;
import java.io.BufferedReader;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.security.KeyManagementException;
//import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;


/**
 *
 * @author Pauline
 */
public class SSL1Simulation {
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException{
        SSLServerSocket ssocket;
        int port = Integer.parseInt(args[0]);
        String nom = args[1];
        String numeroCle = args[2];
        String motDePasse = args[3];
        int numeroNoeudLance = Integer.parseInt(args[4]);
        int [][] noeuds = new int [3][5];
        
        // Données initiales sous forme numNoeud:@IP:numPort
        String delims = "[:]";
        String[] infos = args[5].split(delims);
        
        String certificatsPublics [] = {"biscuit", "volant", "ciment", "arcenciel", "micro"};
        
        try
        {
            JKademliaNode kad1 = new JKademliaNode(nom, new KademliaId("1234567890123456789" + numeroNoeudLance), port);
            System.out.println(ANSI_PURPLE + "Je suis le noeud "+ kad1.getNode().getNodeId() +" avec le port " + kad1.getPort() + ANSI_RESET);
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroCle);
            InputStream targetStream = new FileInputStream(initialFile);
            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert((port + 2), targetStream, motDePasse);
            Thread serveur = new Thread(new Boucle_Serveur(ssocket, numeroNoeudLance));
            serveur.start();
            System.out.println(ANSI_PURPLE + "Je peux commencer à appeler les serveurs" + ANSI_RESET);
            Init_Reseau.initialisation(infos, certificatsPublics);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}

class Accepter_clients implements Runnable {

  private Socket socket;
  int numeroNoeud;
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_RESET = "\u001B[0m";

  public Accepter_clients(Socket s, int noeud){
    socket = s;
    numeroNoeud = noeud;
  }
        
  @Override
  public void run() {
      BufferedReader reader;
      String messageRecu;

    try {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        messageRecu = reader.readLine();
        System.out.println(messageRecu);
        if(messageRecu.equals("PING")){
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Je suis le noeud " + numeroNoeud + " et je suis bien vivant!");
            System.out.println(ANSI_CYAN + "Un nouveau client s'est connecté !" + ANSI_RESET);
        }else{
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Communication avec le noeud " + numeroNoeud);
        }

        socket.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

}

class Boucle_Serveur implements Runnable {
 
    SSLServerSocket ssocket;
    int noeud;
    
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    
    public Boucle_Serveur(SSLServerSocket s, int numeroNoeud){
        ssocket = s;
        noeud = numeroNoeud;
    }

    @Override
    public void run() {
      Socket socket;
      try
      {
        while(true){
            System.out.println(ANSI_CYAN + "Je suis en attente de clients." + ANSI_RESET);
            socket= ssocket.accept();
            System.out.println(ANSI_CYAN + "Connexion cliente reçue." + ANSI_RESET);
            Thread t = new Thread(new Accepter_clients(socket, noeud));
            t.start();
        }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

