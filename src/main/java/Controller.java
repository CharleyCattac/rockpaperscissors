

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Controller {
    private static int KEY_SIZE_BYTES = 32;
    private static List<String> argSet3 = new ArrayList<String>();
    private static List<String> argSet5 = new ArrayList<String>();


    public static void main(String[] args) {
        if (args.length != 3 && args.length != 5) {
            System.out.println("Invalid amount of arguments");
            return;
        }
        List<String> currentArgList = Arrays.asList(args);
        fillArgumentSets();
        if (!argSet3.containsAll(currentArgList)){
            if (!argSet5.containsAll(currentArgList)) {
                System.out.println("Invalid arguments");
                return;
            }
        }

        SecureRandom secureRandom = new SecureRandom();

        int computerMove = secureRandom.nextInt(currentArgList.size());
        String computerMoveString = currentArgList.get(computerMove);

        byte[] randomKey = new byte[KEY_SIZE_BYTES];
        for (int i = 0; i < randomKey.length; i++) {
            if (secureRandom.nextBoolean()) {
                randomKey[i] = (byte) (48 + secureRandom.nextInt(9));
            } else {
                randomKey[i] = (byte) (65 + secureRandom.nextInt(5));
            }
        }

        Mac hasher = null;
        try {
            hasher = Mac.getInstance("HmacSHA256");
            hasher.init(new SecretKeySpec(randomKey, "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println(e);
            return;
        }
        byte[] hash = hasher.doFinal(computerMoveString.getBytes());

        System.out.println("HMAC: " + bytesToHex(hash));

        System.out.println("Available moves:");
        for (int i = 0; i < currentArgList.size(); i++) {
            System.out.println(i+1 + " - " + currentArgList.get(i));
        }
        System.out.println("0 - exit");

        int playerMove = -1;
        do {
            try {
                Scanner in = new Scanner(System.in);
                System.out.print("Enter your move: ");
                playerMove = in.nextInt();
                if (playerMove == 0) {
                    System.out.println("Good bye then!");
                    return;
                } else if (playerMove > 0 && playerMove < currentArgList.size() + 1) {
                    break;
                } else {
                    System.out.println("No such move!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input!");
            }
        } while (playerMove != 0);
        String playerMoveString = currentArgList.get(playerMove - 1);

        System.out.println("Your move: " + playerMoveString);
        System.out.println("Computer move: " + computerMoveString);

        MoveDynamic moveDynamic = MoveDynamic.getInstance();
        int result = moveDynamic.defineResult(playerMoveString, computerMoveString);

        switch (result) {
            case -1:
                System.out.println("You lose!");
                break;
            case 0:
                System.out.println("Draw!");
                break;
            case 1:
                System.out.println("You win!");
                break;
            default:
                System.out.println("Not draw totally...");
        }

        System.out.println("Computer move: " + computerMoveString);

        System.out.println("HMAC key: " + new String(randomKey, StandardCharsets.UTF_8));
    }

    private static void fillArgumentSets() {
        String  rock = "rock",
                paper = "paper",
                scissors = "scissors",
                lizard = "lizard",
                spock = "spock";

        argSet3.add(rock);
        argSet3.add(paper);
        argSet3.add(scissors);

        argSet5.addAll(argSet3);
        argSet5.add(lizard);
        argSet5.add(spock);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}