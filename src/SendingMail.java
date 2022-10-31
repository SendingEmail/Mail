import java.nio.charset.StandardCharsets;
import javax.net.ssl.*;
import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class SendingMail {
    // Credentials
    public static String user;
    public static String pass;
    public static String receiver;
    public static String nickname;
    public static String subject;
    public static String body = "";
    private static DataOutputStream dataOutputStream;
    public static BufferedReader br = null;
    public static void requestInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID:");
        user = scanner.next();
        System.out.print("Password:");
        pass = scanner.next();
        System.out.print("Receiver:");
        receiver = scanner.next();
        System.out.print("NickName:");
        nickname = scanner.next();
        System.out.print("Subject:");
        subject = scanner.next();
        System.out.println("Write the body (end by <end!>:");
        while (true) {
            String tmp = scanner.next();
            if (tmp.equals("end!"))
                break;
            body += tmp;
            body += "\r\n";
        }
    }

    public static void main(String[] args) throws Exception {
        int delay = 1000;

        requestInput();

        String username = Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8));
        String password = Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8));
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket("smtp.naver.com", 465);

        br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

        dataOutputStream = new DataOutputStream(sslSocket.getOutputStream());

        System.out.println("-------------EHLO------------");
        send("EHLO smtp.naver.com\r\n",7);
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
        send("From: "+ nickname + " <" + user + ">\r\n", 0);
        System.out.println("-------------Subject------------");
        send("Subject: " + subject + "\r\n",0);
        System.out.println("----------------To--------------");
        send("To: "+ receiver + "\r\n", 0);
        System.out.println("-------------Email Body------------");
        send("\r\n"+ body + "\r\n",0);
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

        for (int i = 0; i < no_of_response_line; i++) {
            System.out.println("SERVER : " + br.readLine());
        }
    }

}