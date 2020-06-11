

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author Renate Lobach
 * @version 2.1
 */

public class Controller {
    private static int KEY_SIZE_BYTES = 64;

    public static void main(String[] args) {
        List<String> currentArgList = new ArrayList<>(Arrays.asList(args));
        List<String> uniqueArgList = new ArrayList<>(new LinkedHashSet<>(currentArgList));
        if (currentArgList.size() != uniqueArgList.size()) {
            System.out.println("Dublicates found");
            return;
        }
        currentArgList.clear();
        currentArgList.addAll(uniqueArgList);

        if (currentArgList.size() == 1 || currentArgList.size() % 2 != 1){
            System.out.println("Invalid amount of arguments");
            return;
        }

        SecureRandom secureRandom = new SecureRandom();

        byte[] computerMoveByte = new byte[1];
        secureRandom.nextBytes(computerMoveByte);
        int computerMoveByteValue = Math.abs((int) computerMoveByte[0]);
        int computerMove = 1 + computerMoveByteValue * (currentArgList.size()) / 127;
        String computerMoveString = currentArgList.get(computerMove - 1);

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

        int result = defineWinner(playerMove, computerMove, currentArgList.size());

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

        System.out.println("HMAC key: " + new String(randomKey, StandardCharsets.UTF_8));
    }

    private static int defineWinner(int playerMove, int computerMove, int size) {
        if (computerMove > playerMove) {
            return (computerMove - playerMove > (size - 1) / 2) ? 1 : -1;
        } else if (computerMove < playerMove) {
            return (playerMove - computerMove > (size - 1) / 2) ? -1 : 1;
        } else {
            return 0;
        }
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