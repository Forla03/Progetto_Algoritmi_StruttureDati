package Esercizio3;

/*
 * Nome: Francesco
 * Cognome: Forlani
 * Matricola: 0001069583
 * Mail. francesco.forlani5@studio.unibo.it
 */
import java.io.*;
import java.util.*;

public class CDROMAllocator {

    public static void main(String[] args) {
        if (args.length != 1) {
            // Stampa un messaggio di errore e termina l'esecuzione se il nome del file di input non è specificato
            System.err.println("Usage: java Esercizio3 <input_file>");
            System.exit(1);
        }

        String filename = args[0];
        // Legge i file di input e li memorizza in una lista
        List<FileData> files = readInput(filename);
        // Alloca i file ai CD-ROM e ottiene la lista dei CD-ROM utilizzati
        List<CD> cds = allocateFilesToCDs(files, 650);
        // Stampa il risultato dell'allocazione
        printResult(cds);
        
    }

    static class FileData {
        String name;
        int size;

        // Costruttore della classe FileData
        FileData(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    static class CD {
        List<FileData> files;
        int freeSpace;

        // Costruttore della classe CD
        CD(int capacity) {
            this.files = new ArrayList<>();
            this.freeSpace = capacity;
        }

        // Aggiunge un file al CD e aggiorna lo spazio libero rimanente
        void addFile(FileData file) {
            this.files.add(file);
            this.freeSpace -= file.size;
        }
    }

    // Legge i file di input dal file specificato
    private static List<FileData> readInput(String filename) {
        List<FileData> files = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int n = Integer.parseInt(reader.readLine().trim()); // Numero di file
            for (int i = 0; i < n; i++) {
                String[] parts = reader.readLine().split(" ");
                String name = parts[0];
                int size = Integer.parseInt(parts[1]);
                files.add(new FileData(name, size));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return files;
    }

    // Alloca i file ai CD-ROM usando la programmazione dinamica
    private static List<CD> allocateFilesToCDs(List<FileData> files, int capacity) {
        List<CD> cds = new ArrayList<>();

        // Continua finché ci sono file da allocare
        while (!files.isEmpty()) {
            int n = files.size();
            int[][] dp = new int[n + 1][capacity + 1];
            boolean[][] included = new boolean[n + 1][capacity + 1];

            // Popola la tabella dp usando la programmazione dinamica
            for (int i = 1; i <= n; i++) {
                int fileSize = files.get(i - 1).size;                        
                for (int j = 1; j <= capacity; j++) {                    //Sottoproblemi, la più grande quantità di dati che si può mettere
                    if (fileSize <= j) {                                 //dentro un cd considerando i file e j capacity
                        // Se il file può essere incluso nel CD          //soluzione generale nell'ultima cella in basso a destra
                        if (dp[i - 1][j] < dp[i - 1][j - fileSize] + fileSize) {
                            dp[i][j] = dp[i - 1][j - fileSize] + fileSize;
                            included[i][j] = true; // Marca il file come incluso
                        } else {
                            dp[i][j] = dp[i - 1][j]; // Eredita il valore dalla riga precedente
                        }
                    } else {
                        dp[i][j] = dp[i - 1][j]; // Eredita il valore dalla riga precedente se il file non può essere incluso
                    }
                }
            }

            // Crea un nuovo CD e riempilo con i file tracciando indietro la tabella dp
            CD cd = new CD(capacity);
            int remainingCapacity = capacity;

            for (int i = n; i > 0; i--) {
                if (included[i][remainingCapacity]) {
                    FileData file = files.get(i - 1);
                    cd.addFile(file);
                    remainingCapacity -= file.size;
                    files.remove(i - 1); // Rimuove il file dalla lista dei file non ancora allocati
                }
            }

            cds.add(cd); // Aggiunge il CD alla lista dei CD-ROM usati
            
           /* for(int i=0; i<=n;i++) {
            	for(int j=0;j<=capacity;j++)
            		System.out.print(dp[i][j]+" ");
            
            System.out.println();
            }
            System.out.println("*********************");
        }*/
        }
        return cds;
    }

    // Stampa il risultato dell'allocazione dei file sui CD-ROM
    private static void printResult(List<CD> cds) {
        for (int i = 0; i < cds.size(); i++) {
            CD cd = cds.get(i);
            System.out.println("Disco: " + (i + 1));
            for (int j=cd.files.size() - 1; j >= 0; j--) {
                System.out.println(cd.files.get(j).name + " " + cd.files.get(j).size);  //così è come nell'esempio del prof
            }                                                                           
            System.out.println("Spazio libero: " + cd.freeSpace);
            System.out.println();
        }
    }
}

/*
 * Definizione dei sottoproblemi

dp[i][j] è una matrice dove ogni elemento rappresenta un sottoproblema specifico. Nello specifico, dp[i][j] indica la massima capacità riempita usando i 
primi i file senza superare j MB.
i varia da 0 a n (numero totale di file).
j varia da 0 a C (capacità del CD-ROM, in questo caso 650 MB).

i rappresenta il numero di file considerati fino a quel momento. Per esempio, i = 3 significa che stiamo considerando i primi tre file.
j rappresenta la capacità attuale del CD-ROM che stiamo cercando di riempire senza superare.

Soluzioni ai sottoproblemi

Caso base: dp[0][j] = 0
Quando non abbiamo considerato alcun file (i = 0), la capacità riempita è sempre 0 per qualsiasi capacità j. 
Questo è il punto di partenza della nostra matrice.
Se la dimensione del file corrente i (size[i-1]) è minore o uguale alla capacità attuale j,
se decidiamo di non includere il file i, allora dp[i][j] sarà uguale a dp[i-1][j], che rappresenta la massima capacità riempita senza considerare il file i,
altrimenti se decidiamo di includere il file i, allora dobbiamo aggiungere la dimensione del file i alla capacità riempita con i file precedenti, 
rimanendo con una capacità ridotta di j - size[i-1]. In questo caso, dp[i][j] sarà dp[i-1][j - size[i-1]] + size[i-1].
La relazione di ricorrenza combina questi due approcci per ottenere il massimo valore possibile:
dp[i][j] = max(dp[i-1][j], dp[i-1][j - size[i-1]] + size[i-1]).

Calcolo delle soluzioni nei casi base
Caso base: dp[0][j] = 0:
Questo caso è semplice: con 0 file, la capacità riempita è 0 per qualsiasi valore di j.

Calcolo delle soluzioni nel caso generale
Per ogni file i e per ogni capacità j, se la dimensione del file i (size[i-1]) è minore o uguale alla capacità j, 
allora calcoliamo il massimo tra escludere e includere il file.
Altrimenti, ereditiamo il valore di dp[i-1][j] perché non possiamo includere il file i a causa della sua dimensione.

Costo asintotico
Il costo dell'algoritmo è O(n * C), dove n è il numero di file e C è la capacità del CD-ROM.
Questo perché stiamo riempiendo una matrice di dimensioni (n + 1) x (C + 1) e per ogni cella stiamo eseguendo operazioni costanti.
 */
