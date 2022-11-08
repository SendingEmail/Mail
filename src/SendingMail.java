import java.nio.charset.StandardCharsets;
import javax.net.ssl.*;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class SendingMail {
    // 변수선언
    public static String user;
    public static String pass;
    public static String receiver;
    public static String nickname;
    public static String subject;
    public static String body = "";
    private static DataOutputStream dataOutputStream;
    public static BufferedReader br = null;

    //사용자 입력 받기
    public static void requestInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID:");
        user = scanner.next();
        while (!(user.contains("@naver.com"))) { //메일 형식이 @naver.com일 때까지
            System.out.println("메일 형식이 다릅니다. 다시 입력하세요.");
            System.out.print("ID:");
            user = scanner.next();
        }

        System.out.print("Password:");
        pass = scanner.next();
        System.out.print("Receiver:");
        receiver = scanner.next();
        System.out.print("NickName:");
        scanner.nextLine(); //개행문자 삭제
        nickname = scanner.nextLine();
        System.out.print("Subject:");
        subject = scanner.nextLine();
        System.out.println("Write the body (end by <end!>:");
        //이메일 바디 내용 작성
        while (true) {
            String tmp = scanner.nextLine();
            if (tmp.equals("end!")) //end! 입력 시 종료
                break;
            body += tmp; //입력받은 한 라인 씩 body에 추가
            body += "\r\n"; //줄바꿈 개행문자 추가
        }



    }

    public static void main(String[] args) throws Exception {

        int delay = 1000;
        String charSet = "UTF-8" ;

        requestInput(); //사용자 입력 메서드 호출

        String username = Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8));
        String password = Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8));
        String nick = new String(nickname.getBytes(charSet), "8859_1");
        String sub = new String(subject.getBytes(charSet), "8859_1");
        String emailbody = new String(body.getBytes(charSet), "8859_1");
        String msg = "";

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("smtp.naver.com", 465);

        br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        dataOutputStream = new DataOutputStream(sslSocket.getOutputStream());

        msg += "EHLO smtp.naver.com\r\n";
        msg += "AUTH LOGIN\r\n";
        msg += (username + "\r\n");
        msg += (password + "\r\n");
        msg += ("MAIL FROM:<" + user + ">\r\n");
        msg += ("RCPT TO:<" + receiver + ">\r\n");
        msg += "DATA\r\n";
        msg += ("From: "+ nick + " <" + user + ">\r\n");
        msg += ("Subject: " + sub + "\r\n");
        msg += ("To: "+ receiver + "\r\n");
        msg += ("\r\n"+ emailbody + "\r\n");
        msg += ".\r\n";
        msg += "QUIT\r\n";

        send(msg, 14);

        sslSocket.close();
    }
    private static void send(String s, int no_of_response_line) throws Exception
    {
        dataOutputStream.writeBytes(s);
        System.out.println("CLIENT: "+s);
        Thread.sleep(1000);
        String login = null;

        for (int i = 0; i < no_of_response_line; i++) {
            login = br.readLine();
            System.out.println("SERVER : " + login);

            if (login.contains("Username and Password not accepted")) {
                System.out.println("로그인이 안 됩니다.");
                System.exit(0);
            }
        }
    }

}