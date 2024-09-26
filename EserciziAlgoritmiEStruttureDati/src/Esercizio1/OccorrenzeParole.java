package Esercizio1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OccorrenzeParole {
    private static final int K = 1000; // Dimensione iniziale della tabella hash
    private Nodo[] tabella = new Nodo[K]; //tabella è un array di nodi usato per memorizzare le parole e le relative occorrenze

  
    //Il seguente metodo calcola una funzione hash per una data parola
    private int hash(String parola) {       
        int hash = 0; //Inizialmente il valore hash viene impostato su zero
        
        /*Il metodo scorre tutti i caratteri della parola. 
        Per ogni carattere, esegue le seguenti operazioni:
        -Moltiplica il valore hash corrente per un numero primo,come 31. 
        -Aggiunge il valore ASCII del carattere corrente alla somma.
        -Calcola il modulo K del risultato (dove K è la dimensione della tabella hash). 
         Questo assicura che il valore hash risultante rimanga all'interno dei limiti della tabella hash.
         Hashing polinomiale con modulo*/
        for (int i = 0; i < parola.length(); i++) {
            hash = (hash * 31 + parola.charAt(i)) % K;
        }
        return hash;
    }

    public void aggiungiOccorrenze(String parola, int n_occorrenze) {
        parola = parola.toLowerCase(); 
        int index = hash(parola);  
        Nodo nodo = tabella[index];
        
        /*Di seguito viene effettuata una scansione della lista concatenata nella posizione corrispondente nella tabella hash. 
         Se la parola è già presente, viene incrementato il conteggio delle occorrenze e il metodo termina.*/        
        while (nodo != null) {
            if (nodo.parola.equals(parola)) {
                nodo.occorrenze += n_occorrenze;
                return;
            }
            nodo = nodo.next;
        }
        // Se la parola non è stata trovata, aggiungi un nuovo nodo
        Nodo nuovoNodo = new Nodo(parola, n_occorrenze);
        nuovoNodo.next = tabella[index];
        tabella[index] = nuovoNodo;
    }

    public int occorrenzeParola(String parola) {
        parola = parola.toLowerCase(); 
        int index = hash(parola);  
        Nodo nodo = tabella[index];
        while (nodo != null) {
            if (nodo.parola.equals(parola)) {  
                return nodo.occorrenze;
            }
            nodo = nodo.next;
        }
        return 0; 
    }

    private static class Nodo {  //Classe che implementa un nodo 
        String parola;           //Memorizza la parola rappresentata dal nodo
        int occorrenze;          //Memorizza il conteggio delle parole nel testo
        Nodo next;               //Riferimento al nodo successivo

        Nodo(String parola, int occorrenze) {
            this.parola = parola;
            this.occorrenze = occorrenze;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java OccorrenzeParole <file_occorrenze> <file_parole>");
            return;
        }
        String fileOccorrenze = args[0];
        String fileParole = args[1];

        OccorrenzeParole s = new OccorrenzeParole();

        // Leggi i dati dal file delle occorrenze
        try (BufferedReader br = new BufferedReader(new FileReader(fileOccorrenze))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");          
                String parola = parts[0];
                int occorrenze = Integer.parseInt(parts[1]);
                /*Dopo aver diviso tramite uno split la riga ed avere all'indice 0 la parole e all'1 le occorrenze
                 * viene chiamato il metodo aggiungiOccorrenze() e si aggiorna la tabella*/
                s.aggiungiOccorrenze(parola, occorrenze);
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file delle occorrenze: " + e.getMessage());
            return;
        }
        catch(ArrayIndexOutOfBoundsException e){
        	System.out.println("Errore durante la lettura del file delle occorrenze, controllare il formato: " + e.getMessage());
            return;
        }

        // Leggi le parole dal file e visualizza le occorrenze
        try (BufferedReader br = new BufferedReader(new FileReader(fileParole))) {
            String parola;
            while ((parola = br.readLine()) != null) {
                parola = parola.trim();
                int occorrenze = s.occorrenzeParola(parola);
                System.out.println(parola + ", " + occorrenze);
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file delle parole: " + e.getMessage());
        }
    }
}

