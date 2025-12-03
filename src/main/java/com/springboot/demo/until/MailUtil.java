package com.springboot.demo.until;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 2020/6/9 14:22
 * fzj
 */
@Slf4j
public class MailUtil {

    /**
     * 发送邮件
     * @param subject       标题
     * @param text          内容(支持html)
     * @param toMailList    收件人邮箱集合
     */
    public static void sendMail(String subject,String text, List<String> toMailList) {
        String fromMail = "technology@brownsmiss.com";  //账号
        String pwd = "tHyu9pX9aFlMJSBI"; //密码
        int port = 465; //端口
        String host = "smtp.qiye.aliyun.com"; //地址

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable","true");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fromMail, "杭州布诗品牌科技有限公司"));

            Address[] addresses = new Address[toMailList.size()];
            for (int i = 0; i <toMailList.size() ; i++) {
                addresses[i] = new InternetAddress(toMailList.get(i));
            }
            message.addRecipients(javax.mail.Message.RecipientType.TO, addresses);
            message.setSubject(subject);
            message.addHeader("charset", "UTF-8");

            /*添加正文内容*/
            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(text);
            contentPart.setHeader("Content-Type", "text/html; charset=UTF-8");
            multipart.addBodyPart(contentPart);
            message.setContent(multipart);

            message.setSentDate(new Date());
            message.saveChanges();
            Transport transport = session.getTransport("smtp");

            transport.connect(host, port, fromMail, pwd);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            log.error("给用户发送email失败原因：" + e.getMessage());
        }
    }
}
