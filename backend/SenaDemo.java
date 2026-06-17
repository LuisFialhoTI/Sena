import model.*;
import database.*;
import exception.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 *                      SENA - ARQUIVO ÚNICO DE APRESENTAÇÃO
 * =========================================================================
 * Este arquivo foi criado para simplificar sua apresentação acadêmica. Ele
 * contém todos os 12 conceitos em um único lugar, eliminando a necessidade
 * de abrir múltiplos arquivos durante a apresentação.
 * 
 * Para rodar este arquivo no terminal:
 *   javac -cp "backend/lib/*;backend" backend/SenaDemo.java
 *   java -cp "backend/lib/*;backend" SenaDemo
 * =========================================================================
 */
public class SenaDemo {

    // -------------------------------------------------------------------------
    // [05] INTERFACES: Contrato abstrato que define um comportamento
    // -------------------------------------------------------------------------
    interface ServicoSimulacao {
        void rodarSimulacao() throws SenaException;
    }

    // -------------------------------------------------------------------------
    // [06] CLASSES ABSTRATAS: Classe base que não pode ser instanciada diretamente
    // -------------------------------------------------------------------------
    static abstract class SimuladorBase implements ServicoSimulacao {
        protected String nomeDoSimulador;

        // [02] CONSTRUTORES: Método especial de inicialização da classe abstrata
        public SimuladorBase(String nome) {
            this.nomeDoSimulador = nome;
        }

        public void mostrarTitulo() {
            System.out.println("\n--- Iniciando: " + this.nomeDoSimulador + " ---");
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODO PRINCIPAL DE EXECUÇÃO
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("=======================================================");
        System.out.println("            PORTAL SENA - DEMONSTRAÇÃO POO             ");
        System.out.println("=======================================================");

        // -------------------------------------------------------------------------
        // [12] THREAD: Fluxo de execução paralela para não travar a aplicação
        // -------------------------------------------------------------------------
        Thread threadApresentacao = new Thread(() -> {
            try {
                // -------------------------------------------------------------------------
                // [01] CLASSES E OBJETOS: 'proprietario' é um objeto da classe 'Anfitriao'
                // [02] CONSTRUTORES: Chamando o construtor parametrizado para criar o objeto
                // [10] ENUM: TipoConta.ANFITRIAO é passado por baixo do construtor
                // -------------------------------------------------------------------------
                Anfitriao proprietario = new Anfitriao(1, "Luís Felipe", "luis@sena.com", "senha123");

                // -------------------------------------------------------------------------
                // [03] ENCAPSULAMENTO: Acessando o atributo privado 'nome' via método público 'getNome()'
                // -------------------------------------------------------------------------
                System.out.println("[03 - Encapsulamento] Acessando nome privado via getter: " + proprietario.getNome());

                // -------------------------------------------------------------------------
                // [04] HERANÇA: 'Anfitriao' herda todas as características da classe 'Usuario'
                // [07] POLIMORFISMO (Subtipagem): Tratando a subclasse 'Anfitriao' como a superclasse 'Usuario'
                // -------------------------------------------------------------------------
                Usuario usuarioPolimorfico = proprietario;
                System.out.println("[04/07 - Herança e Polimorfismo] Usuário tratado polimorficamente: " + usuarioPolimorfico.getNome());

                // -------------------------------------------------------------------------
                // [08] COLEÇÕES: Criando um ArrayList genérico para armazenar objetos do tipo 'Imovel'
                // -------------------------------------------------------------------------
                List<Imovel> listaImoveis = new ArrayList<>();

                // Criando e adicionando um imóvel à lista
                Imovel apVilaMariana = new Imovel(
                    101, 
                    "Apartamento Vila Mariana", 
                    "Vila Mariana, São Paulo", 
                    "3500", 
                    "imagem.png", 
                    "2 quartos • 1 vaga", 
                    "8.5", 
                    "Seguro,Bem iluminado", 
                    "4.5",
                    "Rua das Flores, 123", 
                    "Excelente apartamento recém reformado.", 
                    "Wi-fi,Garagem,Câmeras", 
                    "8.5,9.0,8.0,8.2", 
                    proprietario.getNome()
                );
                listaImoveis.add(apVilaMariana);
                System.out.println("[08 - Coleções] Imóvel adicionado à lista. Total: " + listaImoveis.size());

                // -------------------------------------------------------------------------
                // [07] POLIMORFISMO (Sobrescrita): O método 'adicionarAvaliacao' atualiza internamente
                // os valores de estrelas e notas do imóvel através da lógica interna da classe Imovel
                // -------------------------------------------------------------------------
                Avaliacao avaliacao = new Avaliacao(1, 101, 5, 9, 8, 9, 8, "Bem iluminada", "Região muito segura!", "17/06/2026");
                apVilaMariana.adicionarAvaliacao(avaliacao);
                System.out.println("[07 - Polimorfismo] Média de estrelas recalculada após avaliação: " + apVilaMariana.getEstrelas());

                // -------------------------------------------------------------------------
                // [09] JAVA IO: Gravando logs em arquivo de texto fisicamente em disco
                // -------------------------------------------------------------------------
                File logFile = new File("log_demonstracao.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                    writer.write("RELATÓRIO DE APRESENTAÇÃO - PORTAL SENA\n");
                    writer.write("Imóvel: " + apVilaMariana.getTitulo() + "\n");
                    writer.write("Média Geral: " + apVilaMariana.getEstrelas() + "\n");
                    System.out.println("[09 - Java IO] Log salvo com sucesso no arquivo: " + logFile.getName());
                }

                // -------------------------------------------------------------------------
                // [11] TRATAMENTO DE EXCEÇÕES: Lançando e capturando exceções com try-catch
                // -------------------------------------------------------------------------
                SimuladorBase simulador = new SimuladorBase("Validação de Credenciais") {
                    @Override
                    public void rodarSimulacao() throws SenaException {
                        mostrarTitulo();
                        System.out.println("Executando validação de segurança da conta...");
                        // Lançando a exceção personalizada SenaException
                        throw new SenaException("Falha simulada: O e-mail informado já está cadastrado.");
                    }
                };

                simulador.rodarSimulacao();

            } catch (SenaException e) {
                System.out.println("[11 - Exceções] Capturada Exceção Personalizada: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("[11 - Exceções] Erro de entrada/saída (Java IO): " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[11 - Exceções] Capturada Exceção Genérica: " + e.getMessage());
            }
        });

        // Iniciando a Thread de demonstração
        threadApresentacao.start();

        try {
            // Espera a finalização da thread de demonstração antes de terminar o programa
            threadApresentacao.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("=======================================================");
        System.out.println("                FIM DA APRESENTAÇÃO                    ");
        System.out.println("=======================================================");
    }
}
