/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;


import java.io.BufferedReader;

import java.net.*;
import javax.net.ssl.SSLServerSocket;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.security.KeyManagementException;
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
    
    // Tables de connaissance du réseau
    public static int [] ports = new int [40];
    public static InetAddress [] adresses = new InetAddress [5];
    public static int compteur = 1;
    public static boolean enFonctionnement = true;
    public static boolean peutEtreFerme = false;
    public static boolean estFermee = false;
    public static int k = 3;
    
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, UnknownHostException, InterruptedException{
        
        SSLServerSocket ssocket = null;
        int port = Integer.parseInt(args[0]);

        // Mot de passe de la clé privée
        String motDePasse = args[1];
        int numeroNoeudLance = Integer.parseInt(args[2]);
        InetAddress adresse = InetAddress.getLocalHost();

        // Connaissances initiales sous forme numNoeud:@IP:numPort
        String delims = "[:]";
        String[] infos = args[3].split(delims);
        
        for(int i = 0; i < ports.length; i++){
            ports[i] = 0;
        }
        
        double peutFonctionner = 0.0;
        double ancienPeutFonctionner;
        int tempsPause = 20000;

        String certificatsPublics [] = {"biscuit", "volant", "ciment", "arcenciel", "micro"};
        
        while(true){
            try
            {
                ancienPeutFonctionner = peutFonctionner;
                peutFonctionner = Math.random();
                if(peutFonctionner >= 0.5){
                    if(ancienPeutFonctionner < 0.5){
                        if(ssocket == null){
                            estFermee = true;
                        }else{
                            if(ssocket.isClosed()){
                                estFermee = true;
                            }
                        }
                        
                        if(estFermee){
                        // Ouverture de la socket sécurisée serveur
                            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/CLE" + numeroNoeudLance);
                            InputStream targetStream = new FileInputStream(initialFile);
                            ssocket = SSLServerSocketKeystoreFactory.getServerSocketWithCert(port, targetStream, motDePasse);
                            enFonctionnement = true;

                            Thread serveur = new Thread(new Boucle_Serveur(ssocket, numeroNoeudLance));
                            serveur.start();

                            System.out.println(ANSI_PURPLE + "Je peux commencer à appeler les serveurs" + ANSI_RESET);

                            Init_Reseau.initialisation(infos, certificatsPublics, ports, adresses, port, adresse, numeroNoeudLance);
                            System.out.println(ANSI_PURPLE + "Je connais maintenant mon réseau!" + ANSI_RESET);
                            afficheTab(ports);

                            Decouverte_Noeuds.discovery(certificatsPublics, ports, adresses, port, adresse, numeroNoeudLance);
                        }
                        if(!estFermee){
                            Operation_Reseau.rafraichissement(numeroNoeudLance, certificatsPublics, port, adresse);
                        }
                    }
                    //Faire envois de messages random
                    System.out.println("Envoi de messages aux autres noeuds");
                }else{
                    enFonctionnement = false;
                    System.out.println("Noeud en pause pour " + tempsPause + " secondes.");
                }
                Thread.sleep(tempsPause);
                estFermee = false;

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void afficheTab (int [] tab){
        for(int i = 0; i < tab.length; i++){
            System.out.println(ANSI_PURPLE + tab[i] + ANSI_RESET);
        }
    }
    
    public static int trouveZone(int numeroNoeud){
        if(0 <= numeroNoeud && numeroNoeud < 10){
            return 0;
        }else if(10 <= numeroNoeud && numeroNoeud < 20){
            return 1;
        }else if(20 <= numeroNoeud && numeroNoeud < 30){
            return 2;
        }else if(30 <= numeroNoeud && numeroNoeud < 40){
            return 3;
        }
        return -1;
    }
    
    
    
    
}

class Accepter_clients implements Runnable {

    private Socket socket;
    int numeroNoeud;
    int compteur;
    
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Accepter_clients(Socket s, int noeud){
        socket = s;
        numeroNoeud = noeud;
        compteur = 1;
    }
        
    @Override
    public void run() {
        BufferedReader reader;
        String messageRecu;

    try {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        
        // Réception du message client
        messageRecu = reader.readLine();
        System.out.println(ANSI_CYAN + messageRecu + ANSI_RESET);
        String delims = "[:]";
        String[] message = messageRecu.split(delims);

        /*while(true){

        }*/

        if(message[0].equals("PING")){
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("REPING:" + numeroNoeud);
            System.out.println(ANSI_CYAN + "Un nouveau client s'est connecté !" + ANSI_RESET);

            // Ajout aux tables de connaissance d'un noeud non connu
            if(SSL1Simulation.ports[Integer.parseInt(message[1])]== 0){
                System.out.println(ANSI_CYAN + "J'ai reçu un ping d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                SSL1Simulation.ports[Integer.parseInt(message[1])] = Integer.parseInt(message[2]);
                SSL1Simulation.adresses[Integer.parseInt(message[1])] = InetAddress.getByName(message[3]);
            }
        }else if(message[0].equals("DISCOVER")){
            // Ajout aux tables de connaissance d'un noeud non connu
            if(SSL1Simulation.ports[Integer.parseInt(message[1])]== 0){
                System.out.println(ANSI_CYAN + "J'ai reçu un discover d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                SSL1Simulation.ports[Integer.parseInt(message[1])] = Integer.parseInt(message[2]);
                SSL1Simulation.adresses[Integer.parseInt(message[1])] = InetAddress.getByName(message[3]);
            }

            //Envoi des noeuds connus par le serveur sous forme noNoeud:Port:Adresse
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            String reponse = "";
            for(int i = 0; i < SSL1Simulation.ports.length; i++){
                System.out.println(SSL1Simulation.ports[i]);
                String addr;
                String [] adresse;
                if((SSL1Simulation.ports[i] != 0) && i != Integer.parseInt(message[1])){
                    addr = SSL1Simulation.adresses[i].toString();
                    adresse = addr.split("/");
                    if(reponse.equals("")){
                        reponse = reponse + i + ":" + String.valueOf(SSL1Simulation.ports[i]) + ":";
                        reponse = reponse + adresse[1];
                    }else{
                        reponse = reponse + ":" + i + ":" + String.valueOf(SSL1Simulation.ports[i]) + ":";
                        reponse = reponse + adresse[1];
                    }
                }
            }
            writer.println(reponse);
            System.out.println(ANSI_CYAN + "Le noeud " + message[1] + " vient de m'envoyer un discovery" + ANSI_RESET);
            System.out.println(ANSI_CYAN + "Je lui réponds " + reponse + ANSI_RESET);
        }else{
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Communication avec le noeud " + numeroNoeud);
        }
        SSL1Simulation.compteur++;
        
        socket.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

}

class Boucle_Serveur implements Runnable {
 
    SSLServerSocket ssocket;
    int noeud;
    boolean aMarche = false;
    
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
            while(SSL1Simulation.enFonctionnement == true){
                // Ecoute sur le port principal pour entendre les demandes de connexion et les gérer
                System.out.println(ANSI_CYAN + "Je suis en attente de clients." + ANSI_RESET);
                socket= ssocket.accept();
                System.out.println(ANSI_CYAN + "Connexion cliente reçue." + ANSI_RESET);
                Thread t = new Thread(new Accepter_clients(socket, noeud));
                t.start();
                aMarche = true;
            }
            if(aMarche == true){
                ssocket.close();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

