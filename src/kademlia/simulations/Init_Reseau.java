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
public class Init_Reseau {
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
        
    public static void initialisation(String [] infos, String [] certificatsPublics) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        int compteur = 0;
        SSLSocket socket;
        BufferedReader reader;
        String motDePasse = "gateau";
        int numeroNoeud;
        int numeroPort;
        InetAddress addr;
        
        
        try
        {
            while(compteur < infos.length){
                numeroNoeud = Integer.parseInt(infos[compteur]);
                System.out.println(ANSI_BLUE + "Je veux communiquer avec le noeud " + numeroNoeud + ANSI_RESET);
                compteur++;
                addr = InetAddress.getByName(infos[compteur]);
                System.out.println(ANSI_BLUE + "Son adresse IP est " + addr + ANSI_RESET);
                compteur++;
                numeroPort = Integer.parseInt(infos[compteur]) + 2;
                System.out.println(ANSI_BLUE + "Son numéro de port est " + numeroPort + ANSI_RESET);
                compteur++;
                motDePasse = certificatsPublics[numeroNoeud];
                System.out.println(ANSI_BLUE + "Le mot de passe est " + motDePasse + ANSI_RESET);
                
                
                
                File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numeroNoeud);
                InputStream targetStream = new FileInputStream(initialFile);
                System.out.println(ANSI_BLUE + "Tentative de connection" + ANSI_RESET);
                socket = SSLSocketKeystoreFactory.getSocketWithCert(addr, numeroPort, targetStream, motDePasse);
                System.out.println(ANSI_GREEN + "Connection établie" + ANSI_RESET);
                PrintWriter writer;
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("PING");
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(ANSI_GREEN + reader.readLine() + ANSI_RESET);
                socket.close();
            }
        }
            
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
