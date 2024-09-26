package Esercizio4;

/*
 * Nome: Francesco
 * Cognome: Forlani
 * Matricola: 0001069583
 * Mail. francesco.forlani5@studio.unibo.it
 */
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import java.util.Vector;

public class AlgoritmoDijkstra {

    static int n; // numero di nodi nel grafo
    static int m; // numero di archi nel grafo
    Vector<LinkedList<Edge>> adjList; // lista di adiacenza

    /**
     * Classe che rappresenta un arco pesato, orientato nel grafo
     */
    private class Edge {
        final int src; // nodo sorgente
        final int dst; // nodo destinazione
        final double w; // peso dell'arco

        /**
         * Costruisce un arco orientato (src, dst) con peso w
         */
        public Edge(int src, int dst, double w) {
            /* L'algoritmo di Dijkstra richiede che i pesi siano non negativi */
            assert (w >= 0.0);

            this.src = src;
            this.dst = dst;
            this.w = w;
        }
    }

    /**
     * Costruisce un oggetto AlgoritmoDijkstra; legge l'input da inputf
     *
     */
    public AlgoritmoDijkstra(String inputf) {
        readGraph(inputf);
    }

    /**
     * Stampa il percorso più breve da |source| a |dst|
     * Se il nodo dst è la sorgente, stampa solo il nodo.
       Se dst è irraggiungibile, stampa "Irraggiungibile".
       Altrimenti, ricorsivamente stampa il percorso attraverso i predecessori.
     *
     */
    protected void print_path(int[] p, int source, int dst) { //O(n) nel caso pessimo
        if (dst == source)
            System.out.print(dst);
        else if (p[dst] < 0)
            System.out.print("Irraggiungibile");
        else {
            print_path(p, source, p[dst]);
            System.out.print("->" + dst);
        }
    }

    /**
     * Stampa l'albero dei percorsi più brevi su standard output.
     * Stampa tutte le distanze e i percorsi più brevi dal nodo sorgente source a tutti gli altri nodi.
       Utilizza print_path per stampare i singoli percorsi.
     *
     *
     */
    public void print_paths(int source, double[] d, int[] p) { //O(n^2)
        System.out.println("Source = " + source);
        System.out.println();
        System.out.println("   s    d         dist path");
        System.out.println("---- ---- ------------ -------------------");
        for (int dst = 0; dst < n; dst++) {
            System.out.printf("%4d %4d %12.4f ", source, dst, d[dst]);
            print_path(p, source, dst);
            System.out.println();
        }
    }

    /**
     * Legge il grafo da un file di input.
       Imposta n come numero di nodi e m come numero di archi.
        Inizializza la lista di adiacenza adjList.
        Aggiunge gli archi al grafo, assicurandosi che i pesi siano non negativi.
     
     */
    private void readGraph(String inputf) { //O(n+m)
        Locale.setDefault(Locale.US);

        try {
            Scanner f = new Scanner(new FileReader(inputf));
            n = f.nextInt();
            m = f.nextInt();

            adjList = new Vector<>(n);

            for (int i = 0; i < n; i++) {
                adjList.add(new LinkedList<>());
            }

            for (int i = 0; i < m; i++) {
                final int src = f.nextInt();
                final int dst = f.nextInt();
                final double weight = f.nextDouble();
                assert (weight >= 0.0);
                adjList.get(src).add(new Edge(src, dst, weight));
                adjList.get(dst).add(new Edge(dst, src, weight)); // aggiungi l'arco nella direzione opposta
            }
        } catch (IOException ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    /**
     * Esegue l'algoritmo dei cammini minimi di Dijkstra a partire dal nodo s
     *
     */
    public Result shortestPaths(int s) {   //O(n+m)log n 
        double[] d = new double[n]; // array delle distanze
        int[] p = new int[n]; // array dei predecessori
        Edge[] sp_edges = new Edge[n]; // array degli archi del cammino minimo
        boolean[] visited = new boolean[n]; // array dei nodi visitati

        MinHeap q = new MinHeap(n); // coda a priorità minima

        Arrays.fill(d, Double.POSITIVE_INFINITY); //inizializza array delle distanze, predeessori e visited con valori non validi
        Arrays.fill(p, -1); 
        Arrays.fill(visited, false);
        d[s] = 0.0;                    //l'array delle distanze all'indice del nodo di partenza ha valore 0 (ovvio)
        for (int v = 0; v < n; v++) {
            q.insert(v, d[v]);        //inizializza minheap con tutte le priorità a infinito
        }        

        while (!q.isEmpty()) {
            final int u = q.min(); 
            q.deleteMin(); 
            visited[u] = true; 
            for (Edge e : adjList.get(u)) { 
                final int v = e.dst; 
                if (!visited[v] && (d[u] + e.w < d[v])) { 
                    d[v] = d[u] + e.w; 
                    q.changePrio(v, d[v]); 
                    p[v] = u; 
                    sp_edges[v] = e; 
                }
            }
        }

        return new Result(d, p); // ritorna le distanze e i predecessori
    }

    /**
     * Classe che rappresenta una coda di priorità minima
     */
    class MinHeap {
        private int[] heap; // array degli elementi
        private double[] priorities; // array delle priorità
        private int[] indices; // array degli indici
        private int size; // dimensione della coda

        public MinHeap(int capacity) {
            heap = new int[capacity];
            priorities = new double[capacity];
            indices = new int[capacity];
            size = 0;
            Arrays.fill(priorities, Double.POSITIVE_INFINITY); // inizializza le priorità a infinito
        }

        public boolean isEmpty() {
            return size == 0; // verifica se la coda è vuota
        }

        public void insert(int v, double priority) {
            heap[size] = v; // aggiungi l'elemento alla coda
            priorities[v] = priority; // assegna la priorità
            indices[v] = size; // memorizza l'indice
            siftUp(size++); // ripristina la proprietà dell'heap
        }

        public int min() {
            return heap[0]; // ritorna l'elemento con la priorità minima
        }

        public void deleteMin() {
            swap(0, --size); // scambia l'elemento minimo con l'ultimo
            siftDown(0); // ripristina la proprietà dell'heap
        }

        public void changePrio(int v, double priority) {
            int i = indices[v];
            double oldPriority = priorities[v];
            priorities[v] = priority; // aggiorna la priorità
            if (priority < oldPriority) {
                siftUp(i); // ripristina la proprietà dell'heap
            } else {
                siftDown(i); // ripristina la proprietà dell'heap
            }
        }

        private void siftUp(int i) {
            while (i > 0 && priorities[heap[parent(i)]] > priorities[heap[i]]) {
                swap(i, parent(i)); // scambia l'elemento con il suo genitore
                i = parent(i); // aggiorna l'indice
            }
        }

        private void siftDown(int i) {
            int minIndex = i;
            int l = leftChild(i);
            if (l < size && priorities[heap[l]] < priorities[heap[minIndex]]) {
                minIndex = l;
            }
            int r = rightChild(i);
            if (r < size && priorities[heap[r]] < priorities[heap[minIndex]]) {
                minIndex = r;
            }
            if (i != minIndex) {
                swap(i, minIndex); // scambia l'elemento con il figlio minore
                siftDown(minIndex); // ripristina la proprietà dell'heap
            }
        }

        private void swap(int i, int j) {
            int tmp = heap[i];
            heap[i] = heap[j];
            heap[j] = tmp;
            indices[heap[i]] = i;
            indices[heap[j]] = j;
        }

        private int parent(int i) {
            return (i - 1) / 2; // ritorna l'indice del genitore
        }

        private int leftChild(int i) {
            return 2 * i + 1; // ritorna l'indice del figlio sinistro
        }

        private int rightChild(int i) {
            return 2 * i + 2; // ritorna l'indice del figlio destro
        }
    }

    /**
     * Classe che rappresenta il risultato dell'algoritmo di Dijkstra
     */
    class Result {
        double[] d; // array delle distanze
        int[] p; // array dei predecessori

        Result(double[] d, int[] p) {
            this.d = d;
            this.p = p;
        }
    }
    
    /**
     * Stampa il costo computazionale dell'algoritmo
     */
    public static void stampaCosto() {
        System.out.println("La lettura del grafo richiede un tempo proporzionale al numero di archi e nodi, quindi ha un costo computazionale di O(" + (n + m) + ")");
        System.out.println("Il costo dell'algoritmo di Dijkstra è O(" + (n + m) + " x " + "log" + n + ") = O(" + ((n + m) * Math.log(n)) + ")");
        System.out.println("La stampa dei risultati richiede un tempo lineare, quindi ha un costo computazionale di O(" + n + "^2)");
        System.out.println("Quindi il costo computazionale totale della soluzione è O(" + (n + m) + ") + O(" + ((n + m) * Math.log(n)) + ") + O(" + n + "^2)");
    }

    /**
     * Metodo principale
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            final int n = 100;
            System.out.printf("%d %d\n", n, n * (n - 1));
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        final double weight = 0.1 + Math.random() * 100;
                        System.out.printf("%d %d %f\n", i, j, weight);
                    }
                }
            }
            return;
        }

        long startTime = System.currentTimeMillis();
        AlgoritmoDijkstra sp = new AlgoritmoDijkstra(args[0]);

        for (int i = 0; i < n; i++) {                             //dominata da n x print_paths, quindi O(n^3)
            System.out.printf("Cammini minimi dal nodo %d:\n", i);  //chiamare dijkstra su tutti i nodi costa O(n((n+m)log n))
            Result result = sp.shortestPaths(i);
            sp.print_paths(i, result.d, result.p);
            System.out.println();
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.printf("Tempo totale: %.3f secondi\n", totalTime / 1000.0);
        stampaCosto();
    }
}
