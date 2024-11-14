package models;

/**
 * Used to calculate the score of throws with 5 dice
 */
public class YatzyResultCalculator {

    private Die[] dice;
    private int[] sum = new int[6];


    public YatzyResultCalculator(Die[] dice) {
        //YatzyResultCalculator constructor.
        this.dice = dice;
        for (Die die : dice) {
            sum[die.getEyes() - 1]++;
        }
    }

    public int upperSectionScore(int eyes) {
        return sum[eyes - 1] * eyes;
    }

    public int upperSectionSum(){
    int totalSum = 0;
    for (int eyes = 1; eyes <= 6; eyes++){
        totalSum += upperSectionScore(eyes);
    }
    return totalSum;
    }

    public int bonus(){
        if (upperSectionSum() >= 63) {
            return 50;
        } else return 0;
    }

    public int onePairScore() {
        //Metode til at finde og udregne summen af 1 par
        int pairMaxValue = 0;
        for (int index = 6; index >= 1; index--) {
            if (sum[index - 1] >= 2) {
                pairMaxValue = index * 2;
                return pairMaxValue;
            }
        }
        return pairMaxValue;
    }

    public int twoPairScore() {
        //Metode til at finde og udregne summen af 2 par
        int pairValue = 0;
        int pairValue2 = 0;

        for (int index = 6; index >= 1; index--) {
            if (sum[index - 1] >= 2) {
                if (pairValue == 0) {
                    pairValue = index * 2;
                } else {
                    pairValue2 = index * 2;
                    break;
                }
            }
        }
        return (pairValue != 0 && pairValue2 != 0) ? pairValue + pairValue2 : 0;
    }

    public int threeOfAKindScore() {
        //Metode for at finde 3 ens og udregne summen
        int threeOfAKind = 0;
        for (int index = 6; index >= 1; index--) {
            if (sum[index - 1] >= 3) {
                threeOfAKind = index * 3;
            }
        }
        return threeOfAKind;
    }

    public int fourOfAKindScore() {
        //Metoden for at finde 4 ens og udregne summen
        int fourOfAKind = 0;
        for (int index = 6; index >= 1; index--) {
            if (sum[index - 1] >= 4) {
                fourOfAKind = index * 4;
            }
        }
        return fourOfAKind;
    }

    public int smallStraightScore() {
        //Metode til at se efter en small straight
        int[] expected = {1, 2, 3, 4, 5};
        return checkStraight(expected) ? 15 : 0;
    }


    public int largeStraightScore() {
        //Metoden til at se efter en large straight
        int[] expected = {2, 3, 4, 5, 6};
        return checkStraight(expected) ? 20 : 0;
    }

    //Boolean metode til at se efter straight
    public boolean checkStraight(int[] expected) {
        boolean[] found = new boolean[7];
        for (Die die : dice) {
            found[die.getEyes()] = true;
        }
        for (int value : expected) {
            if (!found[value]) return false;
        }
        return true;
    }


    public int fullHouseScore() {
        //Metode til at se efter fuldt hus og udregne summen
        int threeOfAKind = 0;
        int pair = 0;
        for (int index = 6; index >= 1; index--) {
            if (sum[index - 1] >= 3 && threeOfAKind == 0) {
                threeOfAKind = index * 3;
            } else if (sum[index - 1] >= 2 && pair == 0) {
                pair = index * 2;
            }
        }
        return (threeOfAKind != 0 && pair != 0) ? threeOfAKind + pair : 0;
    }

    public int chanceScore() {
        int sum = 0;
        for(Die die : dice)
            sum += die.getEyes();
        return sum;
    }

    public int yatzyScore() {
        for (int number : sum) {
            if (number == 5) {
                return 50;
            }
        }
        return 0;
    }
}
