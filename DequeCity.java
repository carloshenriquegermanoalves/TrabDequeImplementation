import java.util.ListIterator;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class DequeCity implements Iterable<Cidade> {

    private int n;  // Contador de elementos
    private No sentinela;  // Nó artificial para marcar início e fim

    public DequeCity() {
        n = 0;
        sentinela = new No();
        sentinela.prox = sentinela;
        sentinela.ant = sentinela;
    }

    private class No {  // Classe Nó
        private Cidade dado;
        private String chave;
        private No prox;
        private No ant;
    }

    public void push_front(String chave, Cidade cidade) {
        // Cria um novo nó e armazena dados
        No no = new No();
        no.dado = cidade;
        no.chave = chave;

        // Define o anterior e próximo do novo nó
        no.ant = sentinela;
        no.prox = sentinela.prox;

        // Ajusta a sentinela e o próximo
        sentinela.prox = no;
        no.prox.ant = no;
        ++n;
    }

    public void push_back(String chave, Cidade cidade) {
        // Cria um novo nó e armazena dados
        No no = new No();
        no.dado = cidade;
        no.chave = chave;

        // Define o anterior e próximo do novo nó
        no.ant = sentinela.ant;
        no.prox = sentinela;

        // Ajusta a sentinela e o próximo
        sentinela.ant = no;
        no.ant.prox = no;
        n++;
    }

    public boolean contains(String chave) {
        if (chave == null) throw new IllegalArgumentException("Argumento de contains() é nulo");
        return get(chave) != null;
    }

    public Cidade get(String chave) {
        if (chave == null) throw new IllegalArgumentException("Argumento de get() é nulo");
        for (No n = sentinela.prox; n != sentinela; n = n.prox)
            if (chave.equals(n.chave)) return n.dado;
        return null;
    }

    public void delete(String chave) {
        if (chave == null) throw new IllegalArgumentException("Argumento de delete() é nulo");
        delete(sentinela.prox, chave);
    }

    private void remove(No no) {
        no.ant.prox = no.prox;
        // Atualiza o próximo nó para apontar para o anterior do que será removido
        no.prox.ant = no.ant;
        --n;
    }

    private void delete(No n, String chave) {
        if (n == sentinela) return;
        if (chave.equals(n.chave)) {
            remove(n);
            return;
        }
        delete(n.prox, chave);
    }

    public void put(String chave, Cidade cidade) {
        if (chave == null) throw new IllegalArgumentException("Primeiro argumento de put() é nulo");
        if (cidade == null) {
            delete(chave);
            return;
        }
        for (No n = sentinela.prox; n != sentinela; n = n.prox) {
            if (chave.equals(n.chave)) {
                n.dado = cidade;
                return;
            }
        }
        push_front(chave, cidade);
    }

    public Cidade pop_front() {
        No no = sentinela.prox;
        Cidade cidade = no.dado;
        // Atualiza o nó anterior para apontar para o próximo do que será removido
        no.ant.prox = no.prox;
        // Atualiza o próximo nó para apontar para o anterior do que será removido
        no.prox.ant = no.ant;
        --n;
        return cidade;
    }

    public Cidade pop_back() {
        No no = sentinela.ant;
        Cidade cidade = no.dado;
        // Atualiza o nó anterior para apontar para o próximo do que será removido
        no.ant.prox = no.prox;
        // Atualiza o próximo nó para apontar para o anterior do que será removido
        no.prox.ant = no.ant;
        --n;
        return cidade;
    }

    public No first() {
        if (sentinela == sentinela.prox) return null;
        return sentinela.prox;
    }

    public boolean isEmpty() {
        return n == 0;
    }  // sentinela == sentinela.prox

    public int size() {
        return n;
    }

    public ListIterator<Cidade> iterator() {
        return new DequeIterator();
    }

    public class DequeIterator implements ListIterator<Cidade> {
        private No atual = sentinela.prox;
        private int indice = 0;
        private No acessadoUltimo = null;

        public boolean hasNext() {
            return indice < n;
        }

        public boolean hasPrevious() {
            return indice > 0;
        }

        public int previousIndex() {
            return indice - 1;
        }

        public int nextIndex() {
            return indice;
        }

        public Cidade next() {
            if (!hasNext()) return null;

            Cidade cidade = atual.dado;
            acessadoUltimo = atual;
            atual = atual.prox;
            indice++;
            return cidade;
        }

        public Cidade previous() {
            if (!hasPrevious()) return null;
            atual = atual.ant;

            Cidade cidade = atual.dado;
            acessadoUltimo = atual;
            indice--;
            return cidade;
        }

        public Cidade get() {
            if (atual == null) throw new IllegalStateException();
            return atual.dado;
        }

        public void set(Cidade c) {
            if (acessadoUltimo == null) throw new IllegalStateException();
            acessadoUltimo.dado = c;
        }

        public void remove() {
            if (acessadoUltimo == null) throw new IllegalStateException();
            acessadoUltimo.ant.prox = acessadoUltimo.prox;
            acessadoUltimo.prox.ant = acessadoUltimo.ant;
            --n;
            if (atual == acessadoUltimo)
                atual = acessadoUltimo.prox;
            else
                indice--;
            acessadoUltimo = null;
        }

        public void add(Cidade c) {
            // Inserir após o atual
            No no = new No();
            no.dado = c;

            no.prox = atual.prox;
            no.ant = atual;

            no.prox.ant = no;
            atual.prox = no;
            n++;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Cidade cidade : this)
            s.append(cidade).append(" ");
        return s.toString();
    }

    public Iterable<String> keys() {
        DequeString deque = new DequeString();
        for (No n = sentinela.prox; n != sentinela; n = n.prox)
            deque.push_back(n.chave);
        return deque;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("\n\nUso: java DequeCity Entrada-02.in Procurar.txt");
            System.exit(0);
        }

        try {
            FileReader in1 = new FileReader(args[0]);
            BufferedReader br = new BufferedReader(in1);
            int total = Integer.parseInt(br.readLine());

            int temperatura = 0;
            StdOut.println(" Total = " + total);
            DequeCity st = new DequeCity();

            for (int i = 0; i < total; i++) {
                String tmp = br.readLine();
                StringTokenizer tk = new StringTokenizer(tmp);
                String chave =  tk.nextToken();
                temperatura = Integer.parseInt(tk.nextToken());
                Cidade minhaCidade =  new Cidade(chave, temperatura);
                st.put(chave, minhaCidade);
            }
            br.close();
            in1.close();
            StdOut.println("-----Testando----- Procurando afterword");
            StdOut.println(st.get("afterword"));
            StdOut.println("-----Testando----- Procurando Feeney");
            StdOut.println(st.get("Feeney"));
            StdOut.println("-----Testando----- Procurando Fee");
            StdOut.println(st.get("Fee"));

            in1 = new FileReader(args[1]);
            br = new BufferedReader(in1);

            System.out.println();
            total = Integer.parseInt(br.readLine());
            for (int i = 0; i < total; i++) {
                String tmp = br.readLine();
                StringTokenizer tk = new StringTokenizer(tmp);
                Cidade minhaCidade = st.get(tk.nextToken());

                if (minhaCidade == null) System.out.println("[Falhou] " + tmp + " não foi encontrada");
                else {
                    System.out.println("[Ok] " + minhaCidade.get_nome() + " foi encontrada. Temperatura lá é: "
                            + minhaCidade.get_temp() + "°F");
                }
            }
            br.close();
            in1.close();
            System.out.println("\n" + st.keys());

        } catch (IOException e) {

        }

    }
}