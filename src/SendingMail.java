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
        System.out.print("ID (e-mail):");
        user = scanner.next();
        while (!(user.contains("@")) || user.endsWith("@")) { //메일 형식일 때까지
            System.out.println("메일 형식이 아닙니다. 다시 입력하세요.");
            System.out.print("ID (e-mail):");
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
        String stmpname = user.split("@")[1];

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("smtp." + stmpname, 465);

        br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        dataOutputStream = new DataOutputStream(sslSocket.getOutputStream());

        System.out.println("-------------EHLO------------");
        send("EHLO smtp." + stmpname + "\r\n",6);
        System.out.println("------------AUTH LOGIN-------------");
        send("AUTH LOGIN\r\n",1);
        System.out.println("-------------USER------------");
        send(username+"\r\n",1);
        System.out.println("-------------PASS------------");
        send(password+"\r\n",1);
        System.out.println("-------------MAIL FROM------------");
        send("MAIL FROM:<" + user + ">\r\n",1);
        System.out.println("-------------RCPT TO------------");
        send("RCPT TO:<" + receiver + ">\r\n",1);
        System.out.println("-------------DATA------------");
        send("DATA\r\n",1);
        System.out.println("-------------FROM------------");
        send("From: "+ nick + " <" + user + ">\r\n", 0);
        System.out.println("-------------Subject------------");
        send("Subject: " + sub + "\r\n",0);
        System.out.println("----------------To--------------");
        send("To: "+ receiver + "\r\n", 0);
        System.out.println("-------------Email Body------------");
        send("\r\n"+ emailbody + "\r\n",0);
        System.out.println("-------------Content------------");
        send(".\r\n",0);
        System.out.println("-------------QUIT------------");
        send("QUIT\r\n",1);

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