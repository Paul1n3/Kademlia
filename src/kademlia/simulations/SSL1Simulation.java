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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;


/**
 *
 * @author Pauline
 */
public class SSL1Simulation {
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    
    // Tables de connaissance du réseau
    public static int [][] ports = new int [4][10];
    public static InetAddress [][] adresses = new InetAddress [4][10];
    
    public static int compteur = 1;
    public static boolean enFonctionnement = true;
    public static boolean peutEtreFerme = false;
    public static boolean estFermee = false;
    public static int k = 3;
    public static int port;
    public static String certificatsPublics [] = {"biscuit", "volant", "ciment", "arcenciel", "micro"};
    
    public static void main(String[] args) throws IOException,
        KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException, UnknownHostException, InterruptedException{
        
        SSLServerSocket ssocket = null;
        port = Integer.parseInt(args[0]);

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
        int max = 5;
        int zone;
        int numeroCase;

        
        while(true){
            try
            {
                System.out.println();
                System.out.println();
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
                            System.out.println(ANSI_PURPLE + "La socket n'a pas été fermée" + ANSI_RESET);
                            ouverture = false;
                        }
                    }else{
                        ouverture = false;
                    }
                    if(ouverture == false){
                        Operation_reseau.rafraichissement(certificatsPublics, port, adresse, numeroNoeudLance);
                        System.out.println(ANSI_PURPLE + "Envoi de messages aux autres noeuds" + ANSI_RESET);
                        int aTrouver = (int) (Math.random() * (max - 0));
                        System.out.println(ANSI_PURPLE + "Je veux trouver le noeud " + aTrouver + ANSI_RESET);
                        
                        zone = SSL1Simulation.trouveZone(aTrouver);
                        numeroCase = SSL1Simulation.convertNumero(aTrouver, zone);
                        if(ports[zone][numeroCase] != 0){
                            System.out.println(ANSI_PURPLE + "Je connais le noeud donc je lui envoie directement le message" + ANSI_RESET);
                        }else if(aTrouver != numeroNoeudLance){
                            Operation_reseau.lookupOperation(aTrouver, numeroNoeudLance);
                        }else{
                            System.out.println("Pas de message à envoyer.");
                        }
                    }else{
                        System.out.println(ANSI_PURPLE + "Pas d'opération à l'ouverture de la connexion" + ANSI_RESET);
                    }
                }else{
                    enFonctionnement = false;
                    System.out.println(ANSI_RED + "Noeud en pause pour " + tempsPause/1000 + " secondes." + ANSI_RESET);
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
            System.out.print(ANSI_PURPLE + "|" + ANSI_RESET);
            for(int j = 0; j < tab[i].length; j++){
                System.out.print(ANSI_PURPLE + tab[i][j] + "|"+ ANSI_RESET);
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
    int numeroNoeudTrouve = -1;
    
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

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
                if(SSL1Simulation.nombreNoeudZone(SSL1Simulation.ports, zone) < SSL1Simulation.k){
                    System.out.println(ANSI_CYAN + "J'ai reçu un ping d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                    SSL1Simulation.ports[zone][numero] = Integer.parseInt(message[2]);
                    SSL1Simulation.adresses[zone][numero] = InetAddress.getByName(message[3]);
                }else{
                    System.out.println(ANSI_CYAN + "Je vérifie qu'un autre noeud est vivant" + ANSI_RESET);
                    boolean trouve = false;
                    for(int j = 0; j < 10; j++){
                        numeroNoeudTrouve = zone*10 + j;
                        if(SSL1Simulation.ports[zone][j] != 0 && numero != numeroNoeud && trouve == false){
                            System.out.println(ANSI_CYAN + "Envoi du ping à " + numeroNoeudTrouve + ANSI_RESET);
                            envoiPing(numeroNoeudTrouve, numeroNoeud, Integer.parseInt(message[2]), InetAddress.getByName(message[3]), Integer.parseInt(message[1]));

                            trouve = true;
                        }
                    }
                }
            }else{
                System.out.println(ANSI_CYAN + "Le noeud " + message[1] + " vérifie que je suis dans le réseau" + ANSI_RESET);
            }
        }
        
        // TRAITEMENT DU MESSAGE DISCOVER
        else if(message[0].equals("DISCOVER")){
            // Ajout aux tables de connaissance d'un noeud non connu
            zone = SSL1Simulation.trouveZone(Integer.parseInt(message[1]));
            numero = SSL1Simulation.convertNumero(Integer.parseInt(message[1]), zone);
            if(SSL1Simulation.ports[zone][numero] == 0){
                System.out.println(ANSI_CYAN + "Un nouveau client s'est connecté ! Je viens de recevoir de lui un DISCOVER" + ANSI_RESET);

                // Ajout aux tables de connaissance d'un noeud non connu
                if(SSL1Simulation.nombreNoeudZone(SSL1Simulation.ports, zone) < SSL1Simulation.k){
                    System.out.println(ANSI_CYAN + "J'ai reçu un DISCOVER d'un noeud inconnu: je le rajoute !" + ANSI_RESET);
                    SSL1Simulation.ports[zone][numero] = Integer.parseInt(message[2]);
                    SSL1Simulation.adresses[zone][numero] = InetAddress.getByName(message[3]);
                }else{
                    System.out.println(ANSI_CYAN + "Je vérifie qu'un autre noeud est vivant" + ANSI_RESET);
                    boolean trouve = false;
                    for(int j = 0; j < 10; j++){
                        numeroNoeudTrouve = zone*10 + j;
                        if(SSL1Simulation.ports[zone][j] != 0 && numero != numeroNoeud && trouve == false){
                            System.out.println(ANSI_CYAN + "Envoi du ping à " + numeroNoeudTrouve + ANSI_RESET);
                            envoiPing(numeroNoeudTrouve, numeroNoeud, Integer.parseInt(message[2]), InetAddress.getByName(message[3]), Integer.parseInt(message[1]));

                            trouve = true;
                        }
                    }
                }
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
        
            
            
        // Réception d'un message LOOKUP
        }else if(message[0].equals("LOOKUP")){
            System.out.println(ANSI_CYAN + "Je viens de recevoir un lookup du noeud " + message[1] + ANSI_RESET);
            zone = SSL1Simulation.trouveZone(Integer.parseInt(message[4]));
            numero = SSL1Simulation.convertNumero(Integer.parseInt(message[4]), zone);
            if(SSL1Simulation.ports[zone][numero] != 0){
                System.out.println(ANSI_GREEN + "Je connais ce noeud!" + ANSI_RESET);
                PrintWriter writer;
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("FOUND:" + numeroNoeud + ":" + message[4] + ":" + SSL1Simulation.ports[zone][numero] + ":" + SSL1Simulation.adresses[zone][numero]);
            }else{
                System.out.println(ANSI_CYAN + "Je ne connais pas ce noeud." + ANSI_RESET);
                PrintWriter writer;
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("NOTFOUND:" + numeroNoeud);
            }
        
            
            
        }else{
            PrintWriter writer;
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Communication avec le noeud " + numeroNoeud);
        }
        SSL1Simulation.compteur++;
        
        socket.close();
    }catch (FileNotFoundException ex) {
        Logger.getLogger(Accepter_clients.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(Accepter_clients.class.getName()).log(Level.SEVERE, null, ex);
    } catch (CertificateException ex) {
        Logger.getLogger(Accepter_clients.class.getName()).log(Level.SEVERE, null, ex);
    } catch (KeyStoreException ex) {
        Logger.getLogger(Accepter_clients.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (KeyManagementException e) {
        e.printStackTrace();
    }
    catch(ConnectException e){
        System.out.println(ANSI_RED + "Noeud indisponible" + ANSI_RESET);
    }
    catch (IOException e) {
        e.printStackTrace();
    }   
    }
    
    public static void envoiPing(int numeroNoeud, int monNoeud, int addPort, InetAddress addAddr, int addNumero) throws FileNotFoundException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException{
        String reponse;
        BufferedReader reader;
        int zone;
        int numero;
        Socket nouvelleConnexion;
        
        try{
            zone = SSL1Simulation.trouveZone(numeroNoeud);
            numero = SSL1Simulation.convertNumero(numeroNoeud, zone);
            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numeroNoeud);
            InputStream targetStream = new FileInputStream(initialFile);
            System.out.println(ANSI_CYAN + "Tentative d'envoi de ping pour voir s'il est toujours vivant au noeud " + numeroNoeud + ANSI_RESET);
            nouvelleConnexion = SSLSocketKeystoreFactory.getSocketWithCert(SSL1Simulation.adresses[zone][numero], SSL1Simulation.ports[zone][numero], targetStream, SSL1Simulation.certificatsPublics[numeroNoeud]);
            System.out.println(ANSI_GREEN + "Connection établie avec le noeud " + numeroNoeud + ANSI_RESET);

            
            // Envoi d'un PING au nouveau noeud connu pour vérifier son existance
            PrintWriter writer;
            writer = new PrintWriter(nouvelleConnexion.getOutputStream(), true);
            writer.println("PING:" + monNoeud + ":" + SSL1Simulation.port + ":" + "127.0.0.1");

            nouvelleConnexion.setSoTimeout(20000);
            reader = new BufferedReader(new InputStreamReader(nouvelleConnexion.getInputStream()));
            reponse = reader.readLine();
            System.out.println(ANSI_CYAN + reponse + ANSI_RESET);

            String delims = "[:]";
            String[] reping = reponse.split(delims);
            // On vérifie si le message renvoyé provient bien de la bonne personne
            if(reping[0].equals("REPING") && reping[1].equals(Integer.toString(numeroNoeud))){
                System.out.println(ANSI_GREEN + "Le REPING vient bien de la bonne personne, je le garde" + ANSI_RESET);
            }else{
                System.out.println(ANSI_RED + "Le noeud n'est plus dans le réseau, je l'enlève de la table de routage" + ANSI_RESET);
                SSL1Simulation.ports[zone][numeroNoeud] = 0;
                System.out.println(ANSI_CYAN + "Je rajoute le nouveau noeud" + ANSI_RESET);
                zone = SSL1Simulation.trouveZone(addNumero);
                numero = SSL1Simulation.convertNumero(addNumero, zone);
                SSL1Simulation.ports[zone][numero] = addPort;
                SSL1Simulation.adresses[zone][numero] = addAddr;
            }
            nouvelleConnexion.close();
        }catch(ConnectException e){
            zone = SSL1Simulation.trouveZone(numeroNoeud);
            numero = SSL1Simulation.convertNumero(numeroNoeud, zone);
            System.out.println("Noeud indisponible");
            System.out.println("Le noeud " + numeroNoeud + "n'est plus dans le réseau, je l'enlève de la table de routage");
            SSL1Simulation.ports[zone][numeroNoeud] = 0;
            System.out.println("Je rajoute le nouveau noeud " + addNumero);
            zone = SSL1Simulation.trouveZone(addNumero);
            numero = SSL1Simulation.convertNumero(addNumero, zone);
            SSL1Simulation.ports[zone][numero] = addPort;
            SSL1Simulation.adresses[zone][numero] = addAddr;
        }
        catch (IOException e) {
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

