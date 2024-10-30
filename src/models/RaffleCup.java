package models;

public class RaffleCup {
    private Die[] dice = new Die[5];

    public RaffleCup() {
        for (int index = 0; index < dice.length - 1; index++) {
            dice[index] = new Die();
        }

    }

    public void throwDice() {
        for (Die die : dice) {
        die.roll();
        }
    }

    public Die[] getDice() {
        return dice;
    }
}
