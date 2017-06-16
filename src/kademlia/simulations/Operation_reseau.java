/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia.simulations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Pauline
 */
public class Operation_reseau {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static BufferedReader reader;
    public static SSLSocket socket;
        
    public static void rafraichissement(String [] certificatsPublics, int port, InetAddress adresse, int noMonNoeud) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, InterruptedException {
        
        int numero;
        int zone;
        int numeroTab;
        boolean check = false;
        
        try
        {
            // Dans chaque zone, on PING un noeud
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < 10; j++){
                    numero = i * 10 + j;
                    if(SSL1Simulation.ports[i][j] != 0 && numero != noMonNoeud && check == false){

                        // Tentative de connexion au noeud de manière sécurisée
                        File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numero);
                        InputStream targetStream = new FileInputStream(initialFile);
                        System.out.println(ANSI_BLUE + "Tentative d'envoi de rafraichissement au noeud " + numero + ANSI_RESET);
                        socket = SSLSocketKeystoreFactory.getSocketWithCert(SSL1Simulation.adresses[i][j], SSL1Simulation.ports[i][j], targetStream, certificatsPublics[numero]);
                        System.out.println(ANSI_GREEN + "Connection établie avec le noeud " + numero + ANSI_RESET);

                        // Envoi du message DISCOVER
                        PrintWriter writer;
                        writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println("PING:" + noMonNoeud + ":" + port + ":" + "127.0.0.1");



                        
                        // Réception de la réponse du PING
                        socket.setSoTimeout(20000);
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String reponse = reader.readLine();
                        System.out.println(ANSI_GREEN + reponse + ANSI_RESET);


                        String delims = "[:]";
                        String[] reping = reponse.split(delims);
                        // On vérifie si le message renvoyé provient bien de la bonne personne
                        if(reping[0].equals("REPING") && reping[1].equals(Integer.toString(numero))){
                            System.out.println(ANSI_GREEN + "Le REPING vient bien de la bonne personne, c'est bon!" + ANSI_RESET);
                        }else{
                            System.out.println("Ce n'est pas la bonne personne qui a répondu");
                        }
                        socket.close();
                        check = true;
                    }
                }
                check = false;
            }
        }catch(NullPointerException e){
            System.out.println(ANSI_RED + "Pas de réponse de la part du noeud contacté" + ANSI_RESET);
        }
        catch(SocketTimeoutException e){
            System.out.println(ANSI_RED + "Timeout" + ANSI_RESET);
        }
        catch(ConnectException e){
            System.out.println(ANSI_RED + "Noeud indisponible" + ANSI_RESET);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }   
    } 
    
    
    public static void lookupOperation(int numeroATrouver, int monNumero) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, InterruptedException{
        int numeroCase;
        int zone;
        int numeroAContacter;
        int numeroTab;
        boolean check = false;
        
        try
        {
            // On envoie un lookup à tous les noeuds de la même zone
            zone = SSL1Simulation.trouveZone(numeroATrouver);
            numeroCase = SSL1Simulation.convertNumero(numeroATrouver, zone);
            for(int i = 0; i < 10; i++){
                numeroAContacter = zone * 10 + i;
                if(SSL1Simulation.ports[zone][i] != 0 && numeroAContacter != monNumero){

                    // Tentative de connexion au noeud de manière sécurisée
                    File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numeroAContacter);
                    InputStream targetStream = new FileInputStream(initialFile);
                    System.out.println(ANSI_BLUE + "Tentative d'envoi de lookup au noeud " + numeroAContacter + ANSI_RESET);
                    socket = SSLSocketKeystoreFactory.getSocketWithCert(SSL1Simulation.adresses[zone][i], SSL1Simulation.ports[zone][i], targetStream, SSL1Simulation.certificatsPublics[numeroAContacter]);
                    System.out.println(ANSI_GREEN + "Connection établie avec le noeud " + numeroAContacter + ANSI_RESET);

                    // Envoi du message LOOKUP
                    PrintWriter writer;
                    writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println("LOOKUP:" + monNumero + ":" + SSL1Simulation.port + ":" + "127.0.0.1" + ":" + numeroATrouver);


                    // Réception de la réponse du LOOKUP
                    socket.setSoTimeout(20000);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String reponse = reader.readLine();
                    System.out.println(ANSI_GREEN + reponse + ANSI_RESET);


                    String delims = "[:]";
                    String[] reping = reponse.split(delims);
                    // On vérifie si le message renvoyé provient bien de la bonne personne
                    if(reping[0].equals("RELOOKUP") && reping[1].equals(Integer.toString(numeroAContacter))){
                        System.out.println(ANSI_GREEN + "Le REPING vient bien de la bonne personne, c'est bon!" + ANSI_RESET);
                        if(reping[2].equals(Integer.toString(numeroATrouver))){
                            System.out.println(ANSI_GREEN + "Le noeud a été trouvé! Je l'ajoute dans ma table de routage" + ANSI_RESET);
                            SSL1Simulation.ports[zone][numeroCase] = Integer.parseInt(reping[3]);
                            SSL1Simulation.adresses[zone][numeroCase] = InetAddress.getByName(reping[4]);
                            System.out.println(ANSI_GREEN + "Envoyer un message au noeud trouvé" + ANSI_RESET);
                        }
                    }
                    else if(reping[0].equals("NOTFOUND") && reping[1].equals(Integer.toString(numeroAContacter))){
                        System.out.println(ANSI_BLUE + "Le noeud " + numeroAContacter + " ne connait pas le noeud que je veux contacter." + ANSI_RESET);
                    }
                    else{
                        System.out.println(ANSI_RED + "Ce n'est pas la bonne personne qui a répondu" + ANSI_RESET);
                    }
                    socket.close();
                    check = true;
                }
                
                check = false;
            }
        }catch(NullPointerException e){
            System.out.println(ANSI_RED + "Pas de réponse de la part du noeud contacté" + ANSI_RESET);
        }
        catch(SocketTimeoutException e){
            System.out.println(ANSI_RED + "Timeout" + ANSI_RESET);
        }
        catch(ConnectException e){
            System.out.println(ANSI_RED + "Noeud indisponible" + ANSI_RESET);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
