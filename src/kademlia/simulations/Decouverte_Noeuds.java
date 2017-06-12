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
import java.net.InetAddress;
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
public class Decouverte_Noeuds {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
        
    public static void discovery(String [] certificatsPublics, int [] ports, InetAddress [] adresses, int port, InetAddress adresse, int noMonNoeud) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        
        SSLSocket socket;
        BufferedReader reader;
        
        try
        {
            for(int i = 0; i < ports.length; i++){
                if(ports[i] != 0 && i != noMonNoeud){
                    File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + i);
                    InputStream targetStream = new FileInputStream(initialFile);
                    System.out.println(ANSI_BLUE + "Envoi de decouverte de nouveaux noeuds au noeud " + i + ANSI_RESET);
                    socket = SSLSocketKeystoreFactory.getSocketWithCert(adresses[i], ports[i], targetStream, certificatsPublics[i]);
                    System.out.println(ANSI_GREEN + "Connection établie avec le noeud " + i + ANSI_RESET);
                    PrintWriter writer;
                    writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println("DISCOVER:" + noMonNoeud + ":" + port + ":" + "127.0.0.1");
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String reponse = reader.readLine();
                    System.out.println(ANSI_GREEN + reponse + ANSI_RESET);
                    
                    //Traitement de la réponse
                    int compteur = 0;
                    int numeroNoeud;
                    int numeroPort;
                    InetAddress address;
                    String delims = "[:]";
                    String[] nouveautes = reponse.split(delims);
                    if(!reponse.equals("")){
                        while(compteur < nouveautes.length){
                            numeroNoeud = Integer.parseInt(nouveautes[compteur]);
                            System.out.println(ANSI_BLUE + "Infos sur le noeud " + numeroNoeud + ANSI_RESET);
                            compteur++;
                            numeroPort = Integer.parseInt(nouveautes[compteur]);
                            System.out.println(ANSI_BLUE + "Son numéro de port est " + numeroPort + ANSI_RESET);
                            compteur++;
                            address = InetAddress.getByName(nouveautes[compteur]);
                            System.out.println(ANSI_BLUE + "Son adresse IP est " + address + ANSI_RESET);
                            compteur++;

                            if(SSL1Simulation.ports[numeroNoeud]== 0){
                                System.out.println(ANSI_BLUE + "J'ai des nouveautés sur le noeud " + numeroNoeud + " : je le rajoute !" + ANSI_RESET);
                                SSL1Simulation.ports[numeroNoeud] = numeroPort;
                                SSL1Simulation.adresses[numeroNoeud] = address;
                            }else{
                                System.out.println(ANSI_BLUE + "Je le connais déjà" + ANSI_RESET);
                            }
                        }
                    }
                    
                    
                    socket.close();
                }
            }
            
                
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    } 
}
