import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {

    private static Map clients;
    private Socket conexao;
    private String clientAtual;
    private static List nomes = new ArrayList();

    public Server(Socket socket) {
        this.conexao = socket;
    }

    public boolean armazena(String newName) {
        for (int i = 0; i < nomes.size(); i++) {
            if (nomes.get(i).equals(newName)) {
                return true;
            }
        }
        nomes.add(newName);
        return false;
    }

    public void remove(String oldName) {
        for (int i = 0; i < nomes.size(); i++) {
            if (nomes.get(i).equals(oldName)) {
                nomes.remove(oldName);
            }
        }
    }

    public static void main(String args[]) {
        clients = new HashMap();
        try {
            ServerSocket server = new ServerSocket(2000);
            System.out.println("Server rodando na porta " + server.getLocalPort());
            
            while (true) {
                Socket conexao = server.accept();
                Thread t = new Server(conexao);
                t.start();
            }
            
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    public void run() {
        try {
            BufferedReader entrada
                    = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            PrintStream saida = new PrintStream(this.conexao.getOutputStream());
            this.clientAtual = entrada.readLine();
            
            if (armazena(this.clientAtual)) {
                saida.println("Este nome ja existe! Conecte novamente com outro Nome.");
                this.conexao.close();
                return;
            } else {
                System.out.println(this.clientAtual + " : Conectado ao Server!");
                saida.println("Conectados: " + nomes.toString());
            }
            
            if (this.clientAtual == null) {
                return;
            }

            clients.put(this.clientAtual, saida);
            String[] mensagem = entrada.readLine().split(":");
            
            while (mensagem != null && !(mensagem[0].trim().equals(""))) {
                send(saida, " escreveu: ", mensagem);
                mensagem = entrada.readLine().split(":");
            }
            
            System.out.println(this.clientAtual + " saiu do bate-papo!");
            String[] out = {" do bate-papo!"};
            send(saida, " saiu", out);
            remove(this.clientAtual);
            clients.remove(this.clientAtual);
            this.conexao.close();
            
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public void send(PrintStream saida, String acao, String[] mensagem) throws IOException {
        Iterator it = clients.entrySet().iterator();
        
        while (it.hasNext()) {
            Map.Entry cliente = (Map.Entry) it.next();
            PrintStream chat = (PrintStream) cliente.getValue();
            
            if (chat != saida) {
                if (mensagem.length == 1) {
                    sendToAll(acao, mensagem, chat);
                } else {
                    
                    if (mensagem[1].equalsIgnoreCase((String) cliente.getKey())) {
                        sendToOne(acao, mensagem, chat, cliente);
                    }
                }
            }
        }
    }

    private void sendToAll(String acao, String[] mensagem, PrintStream chat) throws IOException {
        
        chat.println(this.clientAtual + acao + mensagem[0]);

    }

    private void sendToOne(String acao, String[] mensagem, PrintStream chat, Map.Entry cliente) throws IOException {
        out:
        if (mensagem[1].equalsIgnoreCase((String) cliente.getKey())) {
            chat.println(this.clientAtual + acao + mensagem[0]);
            break out;
        }

    }
}
