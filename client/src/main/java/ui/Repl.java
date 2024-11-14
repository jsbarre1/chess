package ui;

import exceptions.ResponseException;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started ♕\"");
        try {
            client.clearData();
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            client.printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result + "\n");
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }




}
