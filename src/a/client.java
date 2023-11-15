package a;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import a.Response;

public class client {
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

            Socket socket = new Socket(ipAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                System.out.print("계산식(add, min, div, mul)을 입력하세요 (또는 'bye'로 종료): ");
                String expression = new Scanner(System.in).nextLine();

                out.write(expression + "\n");
                out.flush();

                if (expression.equalsIgnoreCase("bye")) {
                    System.out.println("서버와의 연결을 종료합니다.");
                    break;
                }

                // 서버로부터 응답 받기
                String responseString = in.readLine();
                Response response = parseResponse(responseString);

                // 응답 처리
                if (response.getCode() == 200) {
                    System.out.println("계산 결과: " + response.getMessage());
                } else {
                    System.out.println("에러 코드: " + response.getCode());
                    System.out.println("에러 메시지: " + response.getMessage());
                }
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("오류: " + e.getMessage());
        }
    }

    // 서버로부터 받은 응답 문자열을 Response 객체로 파싱하는 메서드
    private static Response parseResponse(String responseString) {
        String[] parts = responseString.split(" ", 2);
        int code = Integer.parseInt(parts[0]);
        String message = parts.length > 1 ? parts[1] : "";
        return new Response(code, message);
    }
}
