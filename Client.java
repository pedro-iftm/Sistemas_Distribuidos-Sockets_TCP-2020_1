import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
public class Client extends Thread {

    private Socket conexao;

    public Client(Socket socket) {
        this.conexao = socket;
    }
    
    public static void main(String args[])
    {
        try {
            Socket socket = new Socket("localhost", 2000);
            PrintStream saida = new PrintStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Digite seu nome: ");
            String nome = teclado.readLine();

            saida.println(nome.toUpperCase());
            Thread thread = new Client(socket);
            thread.start();
            String mensagem;
            while (true)
            {
                System.out.print("Mensagem > ");
                mensagem = teclado.readLine();
                saida.println(mensagem);
            }
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    @Override
    public void run()
    {
        try {
            BufferedReader entrada =  new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));
            String mensagem;
            while (true)
            {
                mensagem = entrada.readLine();
                if (mensagem == null) {
                    System.out.println("Conexão encerrada!");
                    System.exit(0);
                }
                System.out.println();
                System.out.println(mensagem);
                System.out.print(" > ");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... \nIOException: " + e);
        }
    }
}
