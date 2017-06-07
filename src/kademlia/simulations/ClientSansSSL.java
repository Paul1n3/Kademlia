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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;
import kademlia.JKademliaNode;
import kademlia.node.KademliaId;

/**
 *
 * @author Pauline
 */
public class ClientSansSSL {

    public static void main(String [] args)throws IOException, UnknownHostException,
            KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        
        Socket socket;
        BufferedReader reader = null;
       try
        {
            //JKademliaNode kadClient = new JKademliaNode("Chocolat", new KademliaId("12345678901234567890"), 7545);
            //JKademliaNode kad2 = new JKademliaNode("Sucre", new KademliaId("12345678901234567891"), 7513);
            
            //kad1.getServer().sendMessage(kad2.getNode(), new SimpleMessage("Some Message"), new SimpleReceiver());
            
       
            socket = new Socket(InetAddress.getLocalHost(), 1234);
            System.out.println("Tentative de connection");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(reader.readLine());
            socket.close();
        }
            
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }

      
}
