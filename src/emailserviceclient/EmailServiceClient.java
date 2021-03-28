/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailserviceclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author TJ
 */
public class EmailServiceClient {

    public static void main(String[] args) {
        String emailAddress;
        String password;
        String breakingChar = "%%";
        String breakingObjectChar = "¬¬";
        final int SERVER_PORT = 16000;
        try {
            Socket mySocket = new Socket("localhost", SERVER_PORT);

            //send to server
            OutputStream out = mySocket.getOutputStream();
            PrintWriter networkOut = new PrintWriter(new OutputStreamWriter(out));

            //server response
            InputStream in = mySocket.getInputStream();
            Scanner networkIn = new Scanner(new InputStreamReader(in));

            Scanner input = new Scanner(System.in);
            String message = "";
            String response = "";
            int choiceAuth = -1;

            while (choiceAuth != 0) {
                choiceAuth = chooseMenuOptionAuth(input);
                switch (choiceAuth) {
                    case 1: {
                        //LOGIN
                        //authInput(input);
                        System.out.println("Enter email address: ");
                        emailAddress = input.nextLine();
                        System.out.println("Enter password: ");
                        password = input.nextLine();

                        message = "LOGIN" + breakingChar + emailAddress + breakingChar + password;

                        networkOut.println(message);
                        networkOut.flush();
                        response = networkIn.nextLine();
                        //System.out.println(response);
                        if (response.equals("FAILED")) {
                            System.out.println("Failed login");
                        } else if (response.equals("SUCCESS")) {

                            System.out.println("You have successfully logged in");

                            int emailOptionsChoice = -1;
                            while (emailOptionsChoice != 0) {
                                message = "";
                                emailOptionsChoice = emailOptionsChoiceMenu(input);
                                switch (emailOptionsChoice) {
                                    case 1: {
                                        //SEND_MAIL
                                        String moreRecipients = "";
                                        ArrayList<String> receivers = new ArrayList<>();
                                        boolean flag = true;
                                        System.out.print("Recipient name: ");

                                        String recipients = input.nextLine();

                                        //receivers.add(recipient);
                                        while (flag) {
                                            System.out.print("Enter additional recipients or 'done' if complete: ");
                                            moreRecipients = input.nextLine();

                                            if (!moreRecipients.equalsIgnoreCase("done")) {
                                                recipients = recipients.concat(breakingObjectChar + moreRecipients);

                                                //receivers.add(recipient);
                                            } else {
                                                flag = false;
                                            }
                                        }//send_email%%(email stuff)%%bob¬¬jon¬¬sean¬¬me
                                        //System.out.println(recipients);
                                        System.out.print("Message subject: ");
                                        String subject = input.nextLine();
                                        System.out.print("Message body: ");
                                        String content = input.nextLine();

                                        LocalDateTime date = LocalDateTime.now();
                                        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                        String formattedDate = date.format(formater);

                                        //String sender, String sendDate, String subject, String content, String receiver
                                        message = "SEND_MAIL" + breakingChar + emailAddress + breakingChar + formattedDate + breakingChar + subject + breakingChar + content + breakingChar + recipients;
                                        //need loop that will add all recivers and + breakingChar

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        if (response.equals("SUCCESS")) {
                                            System.out.println("Email Sent");
                                        } else if (response.equals("FAILED")) {
                                            System.out.println("Email failed to send");
                                        } else if (components[0].equals("PARTIAL_SUCCESS")) {
                                            System.out.println("Email sent to some, but failed on others.");
                                            System.out.println("Could not send emails to:");
                                            for (int i = 1; i < components.length - 1; i++) {
                                                System.out.println(components[i]);
                                            }
                                        } else {
                                            System.out.println("Unknown error occurred.");
                                        }
                                        break;
                                    }
                                    case 2: {
                                        //GET_UNREAD_EMAILS%%sender¬¬test¬¬var%%sender¬¬test¬¬var%%sender¬¬test¬¬var
                                        message = "GET_UNREAD_EMAILS" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);
                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_NEW_EMAILS")) {
                                            System.out.println("No new Emails!");
                                        } else if (components[0].equals("GET_UNREAD_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println("===========================================================");
                                                System.out.println("Sender: " + email[0]);
                                                System.out.println("Send Date: " + email[1]);
                                                System.out.println("Subject: " + email[2]);
                                                System.out.println("Content: " + email[3]);
                                                System.out.println("===========================================================");
                                            }
                                        } else {
                                            System.out.println("Unknown response");
                                        }
                                        break;
                                    }
                                    case 3: {
                                        //GET_READ_EMAILS
                                        message = "GET_READ_EMAILS" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No new Emails!");
                                        } else if (components[0].equals("GET_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println("===========================================================");
                                                System.out.println("Sender: " + email[0]);
                                                System.out.println("Send Date: " + email[1]);
                                                System.out.println("Subject: " + email[2]);
                                                System.out.println("Content: " + email[3]);
                                                System.out.println("===========================================================");
                                            }
                                        } else {
                                            System.out.println("Unknown response");
                                        }

                                        break;
                                    }
                                    case 4: {
                                        //GET_SPAM
                                        message = "GET_SPAM" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No new Emails!");
                                        } else if (components[0].equals("GET_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println("===========================================================");
                                                System.out.println("Sender: " + email[0]);
                                                System.out.println("Send Date: " + email[1]);
                                                System.out.println("Subject: " + email[2]);
                                                System.out.println("Content: " + email[3]);
                                                System.out.println("===========================================================");
                                            }
                                        } else {
                                            System.out.println("Unknown response");
                                        }
                                        break;
                                    }
                                    case 5: {
                                        //GET SEARCH_EMAILS

                                        System.out.println("Type subject name:");
                                        String subject = input.nextLine();

                                        message = "GET_SEARCH_EMAILS" + breakingChar + emailAddress + breakingChar + subject;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No new Emails!");
                                        } else if (components[0].equals("SEARCH_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println("===========================================================");
                                                System.out.println("Sender: " + email[0]);
                                                System.out.println("Send Date: " + email[1]);
                                                System.out.println("Subject: " + email[2]);
                                                System.out.println("Content: " + email[3]);
                                                System.out.println("===========================================================");
                                            }
                                        } else {
                                            System.out.println("Unknown response");
                                        }
                                        break;
                                    }
                                    case 6: {
                                        //DELETE_EMAIL
                                        message = "GET_READ_EMAILS" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No Emails to delete!");
                                        } else if (components[0].equals("GET_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println(i + ") Sender: " + email[0] + ", Subject:" + email[2]);
                                            }

                                            System.out.println("Select Email to delete");

                                            int deleteEmailChoice = -1;
                                            while (deleteEmailChoice < 1 || deleteEmailChoice > components.length) {
                                                try {
                                                    deleteEmailChoice = input.nextInt();
                                                } catch (InputMismatchException ex) {
                                                    System.out.println("Please enter a number to select from the menu!");
                                                    input.nextLine();
                                                }
                                            }
                                            deleteEmailChoice -= 1;

                                            message = "DELETE_EMAILS" + breakingChar + emailAddress + breakingChar + deleteEmailChoice;

                                            networkOut.println(message);
                                            networkOut.flush();
                                            response = networkIn.nextLine();
                                            System.out.println(response);

                                            if (response.equals("SUCCESS")) {
                                                System.out.println("Email deleted");
                                            } else if (response.equals("FAILURE")) {
                                                System.out.println("Email could not be deleted");
                                            } else {
                                                System.out.println("Unknown response 2");
                                                System.out.println(response);
                                            }

                                        } else {
                                            System.out.println("Unknown response");
                                            System.out.println(response);
                                        }

                                        break;
                                    }

                                    case 7: {
                                        //MARK_SPAM
                                        message = "GET_READ_EMAILS" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No Emails to mark spam!");
                                        } else if (components[0].equals("GET_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println(i + ") Sender: " + email[0] + ", Subject:" + email[2]);
                                            }

                                            System.out.println("Select Email to Mark Spam");

                                            int markSpamChoice = -1;
                                            while (markSpamChoice < 1 || markSpamChoice > components.length - 1) {
                                                try {
                                                    markSpamChoice = input.nextInt();
                                                } catch (InputMismatchException ex) {
                                                    System.out.println("Please enter a number to select from the menu!");
                                                    input.nextLine();
                                                }
                                            }

                                            markSpamChoice -= 1;

                                            message = "MARK_SPAM" + breakingChar + emailAddress + breakingChar + markSpamChoice;

                                            networkOut.println(message);
                                            networkOut.flush();
                                            response = networkIn.nextLine();

                                            if (response.equals("SUCCESS")) {
                                                System.out.println("Email marked spam");
                                            } else if (response.equals("FAILURE")) {
                                                System.out.println("Email could not be marked spam");
                                            } else {
                                                System.out.println("Unknown response");
                                                System.out.println(response);
                                            }
                                        }

                                        break;
                                    }
                                    case 8: {
                                        //DELETE_SPAM

                                        message = "DELETE_SPAM" + breakingChar + emailAddress;
                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        if (response.equals("SUCCESS")) {
                                            System.out.println("All spam was deleted!");
                                        } else if (response.equals("FAILURE")) {
                                            System.out.println("Spam could not be deleted.");
                                        } else {
                                            System.out.println("Unknown response");
                                            System.out.println(response);
                                        }

                                        break;
                                    }
                                    case 9: {
                                        message = "GET_SENT_EMAILS" + breakingChar + emailAddress;

                                        networkOut.println(message);
                                        networkOut.flush();
                                        response = networkIn.nextLine();

                                        String[] components = response.split(breakingChar);

                                        //expected response: GET_UNREAD_EMAILS%%(Email stuff)%%(email stuff) or NO_NEW_EMAILS
                                        if (response.equals("NO_EMAILS")) {
                                            System.out.println("No new Emails!");
                                        } else if (components[0].equals("GET_EMAILS")) {
                                            for (int i = 1; i < components.length; i++) {
                                                String[] email = components[i].split(breakingObjectChar);
                                                System.out.println("===========================================================");
                                                System.out.println("Sender: " + email[0]);
                                                System.out.println("Send Date: " + email[1]);
                                                System.out.println("Subject: " + email[2]);
                                                System.out.println("Content: " + email[3]);
                                                System.out.println("===========================================================");
                                            }
                                        } else {
                                            System.out.println("Unknown response");
                                        }
                                        break;
                                    }
                                    case 0: {
                                        //LOGOUT
                                        System.out.println("Logging out");
                                        break;
                                    }

                                }

                            }
                        } else {

                        }
                        break;
                    }

                    case 2: {
                        //REGISTER
                        //authInput(input);
                        System.out.println("Enter email address: ");
                        emailAddress = input.nextLine();
                        System.out.println("Enter password: ");
                        password = input.nextLine();

                        message = "REGISTER" + breakingChar + emailAddress + breakingChar + password;

                        networkOut.println(message);
                        networkOut.flush();
                        response = networkIn.nextLine();
                        //System.out.println(response);

                        if (response.equals("SUCCESS")) {
                            System.out.println("You have successfully created an email!");
                        } else if (response.equals("FAILURE")) {
                            System.out.println("Register failed.");
                        } else {
                            System.out.println("Unknown error occurred");
                        }
                        break;
                    }

                    case 0: {
                        //CLOSE_CLIENT
                        System.out.println("Closing client");
                        break;
                    }
                }
            }
            mySocket.close();

            System.out.println("Thank you for using this service!");

        } catch (UnknownHostException ex) {
            System.out.println("A problem occurred when attempting to look up host.");
            System.out.println(ex.getMessage());
        } catch (SocketException ex) {
            System.out.println("A problem occurred when creating the socket connecting to the server on port " + SERVER_PORT);
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println("A problem occurred when sending a message to the stream.");
            System.out.println(ex.getMessage());
        }

    }

    public static int chooseMenuOptionAuth(Scanner input) {
        System.out.println("---------------------------------------------------");
        int choiceAuth = -1;
        while (choiceAuth < 0 || choiceAuth > 2) {
            System.out.println("What would you like to do?(Type number):");
            System.out.println("1) Login");
            System.out.println("2) Register");
            System.out.println("0) Exit");

            try {
                choiceAuth = input.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Please enter a number to select from the menu!");
                input.nextLine();
            }
        }
        input.nextLine();
        System.out.println("---------------------------------------------------");
        return choiceAuth;
    }

    public static int emailOptionsChoiceMenu(Scanner input) {
        System.out.println("---------------------------------------------------");
        int emailOptionsChoice = -1;
        while (emailOptionsChoice < 0 || emailOptionsChoice > 9) {
            System.out.println("What would you like to do?(Type number):");
            System.out.println("1) Send Email");
            System.out.println("2) Check for new emails");
            System.out.println("3) Get all emails");
            System.out.println("4) Check spam emails");
            System.out.println("5) Search Email");
            System.out.println("6) Delete email");
            System.out.println("7) Mark email as spam");
            System.out.println("8) Delete all spam");
            System.out.println("9) Get sent emails");
            System.out.println("0) Exit");

            try {
                emailOptionsChoice = input.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Please enter a number to select from the menu!");
                input.nextLine();
            }
        }
        input.nextLine();
        System.out.println("---------------------------------------------------");
        return emailOptionsChoice;
    }

//    public static int authInput(Scanner input) {
//        System.out.println("Enter EmailAddress: ");
//        emailAddress = input.nextLine();
//        System.out.println("Enter password: ");
//        password = input.nextLine();
//    }
}
