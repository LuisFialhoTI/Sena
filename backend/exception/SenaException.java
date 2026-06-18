package exception;

public class SenaException extends Exception {

    // 1º Construtor: Recebe apenas a mensagem de erro (1 parâmetro)
    public SenaException(String message) {
        super(message);
    }

    // 2º Construtor (Sobrecarga): Recebe a mensagem e a causa original do erro (2 parâmetros)
    public SenaException(String message, Throwable cause) {
        super(message, cause);
    }
}
