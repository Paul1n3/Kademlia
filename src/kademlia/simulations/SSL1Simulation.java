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
    public static int [][] ports = new int [4][10];
    public static InetAddress [][] adresses = new InetAddress [4][10];
    
    public static int compteur = 1;
    public static boolean enFonctionnement = true;
    public static boolean peutEtreFerme = false;
    public static boolean estFermee = false;
    public static int k = 1;
    
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
            for(int j = 0; j < ports[i].length; j++){
                ports[i][j] = 0;
            }
        }
        
        double peutFonctionner = 0.0;
        double ancienPeutFonctionner;
        int tempsPause = 20000;
        boolean ouverture = false;

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
                            ouverture = true;
                        }else{
                            System.out.println("La socket n'a pas été fermée");
                            ouverture = false;
                        }
                    }else{
                        ouverture = false;
                    }
                    if(ouverture == false){
                        Operation_reseau.rafraichissement(certificatsPublics, port, adresse, numeroNoeudLance);
                    }
                    //Faire envois de messages random
                    System.out.println("Envoi de messages aux autres noeuds");
                }else{
                    enFonctionnement = false;
                    System.out.println("Noeud en pause pour " + tempsPause/1000 + " secondes.");
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
    
    public static void afficheTab (int [][] tab){
        for(int i = 0; i < tab.length; i++){
            for(int j = 0; j < tab[i].length; j++){
                System.out.print(ANSI_PURPLE + tab[i][j] + ANSI_RESET);
            }
            System.out.println();
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
    
    public static int convertNumero(int numeroNoeud, int zone){
        int dizaine = zone * 10;
        return numeroNoeud - dizaine;
    }
    
    public static int nombreNoeudZone(int [][] tab, int zone){
        int compteur = 0;
        for(int i = 0; i < tab.length; i++){
            if(tab[zone][i] != 0){
                compteur++;
            }
        }
        return compteur;
    }
    
    
    
}

class Accepter_clients implements Runnable {

    private Socket socket;
    int numeroNoeud;
    int compteur;
    int zone;
    int numero;
    
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

        // TRAITEMENT DU MESSAGE PING
        if(message[0].equals("PING")){
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("REPING:" + numeroNoeud);
            
            zone = SSL1Simulation.trouveZone(Integer.parseInt(message[1]));
            numero = SSL1Simulation.convertNumero(Integer.parseInt(message[1]), zone);
            if(SSL1Simulation.ports[zone][numero] == 0){
                System.out.println(ANSI_CYAN + "Un nouveau client s'est connecté !" + ANSI_RESET);

                // Ajout aux tables de connaissance d'un noeud non connu
                zone = SSL1Simulation.trouveZone(Integer.parseInt(message[1]));
                numero = SSL1Simulation.convertNumero(Integer.parseInt(message[1]), zone);
                if(SSL1Simulation.nombreNoeudZone(SSL1Simulation.ports, zone) < SSL1Simulation.k){
                    System.out.println(ANSI_CYAN + "J'ai reçu un ping d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                    SSL1Simulation.ports[zone][numero] = Integer.parseInt(message[2]);
                    SSL1Simulation.adresses[zone][numero] = InetAddress.getByName(message[3]);
                }else{
                    System.out.println("Je vérifie que les autres noeuds sont vivants");
                }
            }else{
                System.out.println("Le noeud " + message[1] + " vérifie que je suis dans le réseau");
            }
        }
        
        // TRAITEMENT DU MESSAGE DISCOVER
        else if(message[0].equals("DISCOVER")){
            // Ajout aux tables de connaissance d'un noeud non connu
            zone = SSL1Simulation.trouveZone(Integer.parseInt(message[1]));
            numero = SSL1Simulation.convertNumero(Integer.parseInt(message[1]), zone);
            if(SSL1Simulation.ports[zone][numero]== 0 && SSL1Simulation.nombreNoeudZone(SSL1Simulation.ports, zone) < SSL1Simulation.k){
                System.out.println(ANSI_CYAN + "J'ai reçu un discover d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                SSL1Simulation.ports[zone][numero] = Integer.parseInt(message[2]);
                SSL1Simulation.adresses[zone][numero] = InetAddress.getByName(message[3]);
            }

            //Envoi des noeuds connus par le serveur sous forme noNoeud:Port:Adresse
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            String reponse = "";
            int compteur = 0;
            String addr;
            String [] adresse;
            int numero;
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < SSL1Simulation.ports.length; j++){
                    numero = i * 10 + j;
                    //System.out.println(SSL1Simulation.ports[i]);
                    if((SSL1Simulation.ports[i][j] != 0) && numero != Integer.parseInt(message[1]) && compteur < 2){
                        addr = SSL1Simulation.adresses[i][j].toString();
                        adresse = addr.split("/");
                        if(reponse.equals("")){
                            reponse = reponse + numero + ":" + String.valueOf(SSL1Simulation.ports[i][j]) + ":";
                            reponse = reponse + adresse[1];
                        }else{
                            reponse = reponse + ":" + numero + ":" + String.valueOf(SSL1Simulation.ports[i][j]) + ":";
                            reponse = reponse + adresse[1];
                        }
                        compteur ++;
                    }
                }
                compteur = 0;
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

