package a;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class server {
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                System.out.println("클라이언트와 연결되었습니다.");

                while (true) {
                    String inputMessage = in.readLine();
                    if (inputMessage == null || inputMessage.equalsIgnoreCase("bye")) {
                        System.out.println("클라이언트에서 연결을 종료하였음");
                        break; // "bye"를 받거나 연결이 종료되면 루프 탈출
                    }

                    String result = calc(inputMessage);
                    out.write(result + "\n");
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String calc(String exp) {
        StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() != 3)
            return "error: too many arguments";

        String opcode = st.nextToken().toLowerCase(); // 연산자를 소문자로 변경
        int op1 = Integer.parseInt(st.nextToken());
        int op2 = Integer.parseInt(st.nextToken());

        switch (opcode) {
            case "add":
                return Integer.toString(op1 + op2);
            case "min":
                return Integer.toString(op1 - op2);
            case "mul":
                return Integer.toString(op1 * op2);
            case "div":
                if (op2 == 0) {
                    return "error: divide by zero";
                }
                return Integer.toString(op1 / op2);
            default:
                return "error";
        }
    }

    public static void main(String[] args) {
        try {
            String ipAddress = "127.0.0.1";
            int port = 8080;

            // info.txt 파일이 존재하는 경우, 파일에서 아이피와 포트번호 읽어오기
            File infoFile = new File("info.txt");
            if (infoFile.exists()) {
                Scanner scanner = new Scanner(infoFile);
                ipAddress = scanner.next();
                port = scanner.nextInt();
                scanner.close();
            }

            ServerSocket listener = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));

            System.out.println("연결을 기다리고 있습니다.....");

            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while (true) {
                Socket socket = listener.accept();
                Runnable clientHandler = new ClientHandler(socket);
                executorService.execute(clientHandler); // 쓰레드 풀에서 실행
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("입력 형식 오류: 정수를 입력하세요.");
        }
    }
}
