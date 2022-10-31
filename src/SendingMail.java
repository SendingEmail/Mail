import java.nio.charset.StandardCharsets;
import javax.net.ssl.*;
import java.io.*;
import java.util.Base64;

public class SendingMail {
    // Credentials
    public static String user = "";
    public static String pass = "";
    private static DataOutputStream dataOutputStream;
    public static BufferedReader br = null;

    public static void main(String[] args) throws Exception {
        int delay = 1000;

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
        send("MAIL FROM:<smc9919@naver.com>\r\n",1);
        System.out.println("-------------RCPT TO------------");
        send("RCPT TO:<vdhsfkdls@naver.com>\r\n",1);
        System.out.println("-------------DATA------------");
        send("DATA\r\n",1);
        System.out.println("-------------FROM------------");
        send("From: mincshin <smc9919@naver.com>\r\n", 0);
        System.out.println("-------------Subject------------");
        send("Subject: Email test\r\n",0);
        System.out.println("----------------To--------------");
        send("To: vdhsfkdls@naver.com\r\n", 0);
        System.out.println("-------------Email Body------------");
        send("\r\nhello world!!\r\n", 0);
        send("Email Body\r\n",0);
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