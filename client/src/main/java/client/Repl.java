package client;

public class Repl {

    private final PreloginClient client;

    public Repl(String serverUrl) {
        client = new PreloginClient();
    }

    public void run() {
        System.out.println("♕ Welcome to chess ♕");
        System.out.print(client.help());
    }
}
