package com.baticuisine;

import java.util.Scanner;

public class codes {
    static int number,AdditionalNumber,result,iteration=0;
    static char operator;
    static String loop = "no";
    public static int addNumbers() {
        iteration++;
        while (loop.equalsIgnoreCase("no")) {
            System.out.println("Enter a number: ");
            Scanner scanner = new Scanner(System.in);
            number = scanner.nextInt();
            System.out.println("Enter a number: ");
            AdditionalNumber = scanner.nextInt();
            System.out.println("Enter an operator: ");
            operator = scanner.next().charAt(0);
            System.out.println("Quit(yes/no) :  ");
            loop = scanner.next();
            switch (operator) {
                case '+':

                    result = number + AdditionalNumber;
                    break;
                case '-':
                    result = number - AdditionalNumber;
                    break;
                case '*':
                    result = number * AdditionalNumber;
                    break;
                case '/':
                    result = number / AdditionalNumber;
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }

                if(iteration==1){
                    return result;
                }else{
                    return result+=result;
                }
    }
    public static void main(String[] args) {
        int printResult = addNumbers();
        System.out.println("the result is : " + printResult);
    }
}
