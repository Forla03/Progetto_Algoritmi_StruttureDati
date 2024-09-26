package Esercizio2;

/*
 * Nome: Francesco
 * Cognome: Forlani
 * Matricola: 0001069583
 * Mail. francesco.forlani5@studio.unibo.it
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class VisitaScacchiera {

    private static class Pair {  //Classe usata per rappresentare una coppia di valori interi
        int r, c;        //r e c rappresentano una cella della scacchiera (r=riga, c=colonna)

        Pair(int r, int c) {
            this.r = r;     
            this.c = c;
        }
    }

    // Metodo per controllare se una posizione è valida sulla scacchiera
    //Il metodo controlla se la posizione (r, c) si trova all'interno dei limiti della scacchiera definiti dalle dimensioni n e m
    //Il metodo verifica se la riga r è compresa tra 0 (incluso) e n (escluso) e se la colonna c è compresa tra 0 (incluso) e m (escluso)
    private static boolean isValid(int r, int c, int n, int m) {
        return r >= 0 && r < n && c >= 0 && c < m;
    }

    // Metodo per eseguire la BFS
    //Viene applicata ad una matrice bidimensionale che però rappresenta una struttura simile ad un grafo
    //Ogni casella è un nodo del grafo, le mosse del cavallo sono gli archi
    
    private static boolean bfs(char[][] board, int start_r, int start_c) {  
    	//board è una matrice bidimensionale di caratteri che rappresenta la configurazione della scacchiera
        //start_r è la riga di partenza del cavallo
        //start_c è la colonna di partenza del cavallo
        int n = board.length;
        int m = board[0].length;       
        boolean[][] visited = new boolean[n][m];             
        int[][] moves = { { -2, -1 }, { -1, -2 }, { 1, -2 }, { 2, -1 }, { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 } };
        //moves rappresenta le possibili mosse del cavallo
        
        visited[start_r][start_c] = true;                  
        LinkedList<Pair> coda = new LinkedList<>();
        coda.add(new Pair(start_r, start_c));

        //Finché la coda non è vuota, estrae la casella corrente dalla coda e controlla tutte le mosse possibili del cavallo da quella casella
        while (!coda.isEmpty()) {
            Pair current = coda.poll();
            int r = current.r;
            int c = current.c;

            for (int[] move : moves) {
                int nr = r + move[0];
                int nc = c + move[1];

                //Per ogni mossa possibile, verifica se la nuova posizione è all'interno dei limiti della scacchiera 
                //e se non è stata visitata in precedenza
                if (isValid(nr, nc, n, m) && !visited[nr][nc] && board[nr][nc] != 'X') {
                	//Se la nuova posizione è valida e non è già stata visitata, 
                	//la segna come visitata, la marca sulla scacchiera e la aggiunge alla coda per esplorazione successiva
                    visited[nr][nc] = true;
                    board[nr][nc]='c';
                    coda.add(new Pair(nr, nc));
                }
            }
        }
               

        // Controlla se tutte le caselle vuote sono state visitate,
        // se non lo sono restituisce false, altrimenti true
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!visited[i][j] && board[i][j] != 'X')  { //!!!!!
                    return false;
                }
            }
        }
        return true;
    }
    
  
    public static void main(String[] args) throws IOException {
        // Lettura della scacchiera da file di input
        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        int n = Integer.parseInt(reader.readLine().trim());
        int m = Integer.parseInt(reader.readLine().trim());
        char[][] board = new char[n][m];

        //Crea la matrice che rappresenta scacchiera leggendo dal file
        for (int i = 0; i < n; i++) {
            String line = reader.readLine().trim();
            for (int j = 0; j < m; j++) {
                board[i][j] = line.charAt(j);
            }
        }
        reader.close();

        // Trova la posizione iniziale del cavallo
        int start_r = -1, start_c = -1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (board[i][j] == 'C') {
                    start_r = i;
                    start_c = j;
                    break;
                }
            }
        }
        
        boolean result = bfs(board, start_r, start_c);

        // Stampa la scacchiera e il risultato
        for (int i = 0; i < n; i++) {
            System.out.println(board[i]);
        }
        System.out.println(result);
    }
}