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
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static int numeroNoeud;
    public static int compteur = 0;
    public static SSLSocket socket;
    public static BufferedReader reader;
    public static String motDePasse = "";
    public static int numeroPort;
    public static InetAddress addr;
    public static String reponse;
    public static int zone;
    public static int numero;
    
    public static void initialisation(String [] infos, String [] certificatsPublics, int [][] ports, InetAddress [][] adresses, int port, InetAddress adresse, int noMonNoeud) throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException, InterruptedException {
        
            
            while(compteur < infos.length){
                // On récupère les numéros de port et adresse de chaque noeud
                numeroNoeud = Integer.parseInt(infos[compteur]);
                System.out.println(ANSI_BLUE + "Je veux communiquer avec le noeud " + numeroNoeud + ANSI_RESET);
                compteur++;
                addr = InetAddress.getByName(infos[compteur]);
                System.out.println(ANSI_BLUE + "Son adresse IP est " + addr + ANSI_RESET);
                compteur++;
                numeroPort = Integer.parseInt(infos[compteur]);
                System.out.println(ANSI_BLUE + "Son numéro de port est " + numeroPort + ANSI_RESET);
                compteur++;
                motDePasse = certificatsPublics[numeroNoeud];
                System.out.println(ANSI_BLUE + "Le mot de passe est " + motDePasse + ANSI_RESET);
                
                //Dans un thread séparé
                Thread thread;
                thread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        
                        try
                        {
                            // On essaie de se connecter de façon sécurisée
                            File initialFile = new File("/Users/Pauline/Desktop/Kademlia/src/kademlia/certificat" + numeroNoeud);
                            InputStream targetStream = new FileInputStream(initialFile);
                            System.out.println(ANSI_BLUE + "Tentative de connection" + ANSI_RESET);
                            socket = SSLSocketKeystoreFactory.getSocketWithCert(addr, numeroPort, targetStream, motDePasse);
                            System.out.println(ANSI_GREEN + "Connection établie" + ANSI_RESET);
                            
                            // Envoi d'un PING au nouveau noeud connu pour vérifier son existance
                            PrintWriter writer;
                            writer = new PrintWriter(socket.getOutputStream(), true);
                            writer.println("PING:" + noMonNoeud + ":" + port + ":" + "127.0.0.1");
                            
                            socket.setSoTimeout(20000);
                            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            reponse = reader.readLine();
                            System.out.println(ANSI_BLUE + reponse + ANSI_RESET);
                            
                            String delims = "[:]";
                            String[] reping = reponse.split(delims);
                            // On vérifie si le message renvoyé provient bien de la bonne personne
                            if(reping[0].equals("REPING") && reping[1].equals(Integer.toString(numeroNoeud))){
                                System.out.println(ANSI_GREEN + "Le REPING vient bien de la bonne personne, je l'ajoute dans ma table" + ANSI_RESET);
                                // On ajoute ce noeud dans notre connaissance du réseau
                                zone = SSL1Simulation.trouveZone(numeroNoeud);
                                numero = SSL1Simulation.convertNumero(numeroNoeud, zone);
                                ports[zone][numeroNoeud] = numeroPort;
                                adresses[zone][numeroNoeud] = addr;
                            }
                            socket.close();
                        }
                        catch(SocketTimeoutException e){
                            System.out.println(ANSI_GREEN + "Timeout" + ANSI_RESET);
                        }
                        catch(ConnectException e){
                            System.out.println("Noeud indisponible");
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        catch(KeyManagementException e){
                            System.out.println("Problème lors de la connection");
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(Init_Reseau.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (CertificateException ex) {
                            Logger.getLogger(Init_Reseau.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (KeyStoreException ex) {
                            Logger.getLogger(Init_Reseau.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                thread.start();
                thread.join();
                
            }
            
    }
    
}
