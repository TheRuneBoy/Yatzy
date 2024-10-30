package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Die;
import models.YatzyResultCalculator;

public class YatzyGui extends Application {
    private TextField[][] scoreFields;
    private Label[] totalScoreLabels;
    private int rollCount;
    private boolean[] diceLocked;
    private boolean[] isScored;

    @Override
    public void start(Stage primaryStage) {
        //Skaber layout for scorebord
        GridPane scorePane = new GridPane();
        scorePane.setVgap(10);
        scorePane.setHgap(20);

        //Tilføj spiller
        scorePane.add(new Label("Player 1"), 1, 0);
        scorePane.add(new Label("Player 2"), 2, 0);

        // Tilføj kategorierne for score mulighederne
        String[] categories = {"1'ere", "2'ere", "3'ere", "4'ere", "5'ere", "6'ere", "Sum", "Bonus", "Et par", "To par", "3 ens", "4 ens", "Large straight", "Small straight", "Full house", "Chance", "Yatzy"};
        scoreFields = new TextField[categories.length][2];
        totalScoreLabels = new Label[2];
        isScored = new boolean[categories.length];

        for (int i = 0; i < categories.length; i++) {
            //Tilføjer kategori label
            scorePane.add(new Label(categories[i]), 0, i + 1);

            //Opretter et scorefelt pr spiller
            for (int j = 0; j < 2; j++) {
                TextField scoreField = new TextField();
                scoreField.setOnMouseClicked(event -> this.chooseFieldAction(event));
                scoreField.setMaxWidth(50);
                scorePane.add(scoreField, j + 1, i + 1);
                scoreFields[i][j] = scoreField;
                scoreField.textProperty().addListener((obs, oldVal, newVal) -> updateTotalScores());
            }
        }


        // Tilføj en række for total score
        scorePane.add(new Label("Total"), 0, categories.length + 1);
        for (int j = 0; j < 2; j++) {
            Label totalScoreLabel = new Label("0");
            scorePane.add(totalScoreLabel, j + 1, categories.length + 1);
            totalScoreLabels[j] = totalScoreLabel;
        }

        // Tilføj en knap til at åbne pop-up-vinduet til terningerne
        Button rollDiceButton = new Button("Rul terningerne");
        rollDiceButton.setOnAction(event -> showDicePopup());
        scorePane.add(rollDiceButton, 0, categories.length + 2, 3, 1);
        scorePane.setAlignment(Pos.CENTER);

        // Opsæt scenen og vis hovedvinduet
        Scene scene = new Scene(scorePane, 500, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yatzy Game");
        primaryStage.show();
    }

    private void showDicePopup() {
        rollCount = 0;
        diceLocked = new boolean[5];

        //Laver et popup vindue til terningerne
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Rull terninger");

        //Layout til terningerne
        GridPane dicePane = new GridPane();
        dicePane.setHgap(10);
        dicePane.setPadding(new Insets(30));

        // Tilføj terninger som knapper, så de kan vælges
        for (int i = 0; i < 5; i++) {
            int index = i;
            Button dieButton = new Button("1"); // Start med '1' som terningværdi
            dieButton.setPrefSize(50, 50);

            // Klik på terning for at markere/afmarkere den
            dieButton.setOnAction(event -> {
                diceLocked[index] = !diceLocked[index];
                if (diceLocked[index]) {
                    dieButton.setStyle("-fx-border-color: blue");
                } else {
                    dieButton.setStyle(""); //Fjerner markeringen
                }
            });
            dicePane.add(dieButton, i, 0);
        }

        // Tilføj knap til at rulle terningerne
        Button rollButton = new Button("Rul terningerne");
        rollButton.setOnAction(event -> {
            if (rollCount < 3) {
                rollDice(dicePane);
                rollCount++;
                if (rollCount == 3) {
                    rollButton.setDisable(true);
                }
            }
        });
        dicePane.add(rollButton, 0, 2, 5, 1);

        // Tilføj scenen til pop-up vinduet
        Scene popupScene = new Scene(dicePane, 400, 200);
        popupStage.setScene(popupScene);
        popupStage.show();
    }


    private void rollDice(GridPane dicePane) {
        // Rul terningerne - opdater kun knapperne, hvis de ikke er låst
        Die[] dice = new Die[5];
        for (int i = 0; i < 5; i++) {
            Button dieButton = (Button) dicePane.getChildren().get(i);
            if (!diceLocked[i]) { // Tjek om terningen er låst
                int newValue = (int) (Math.random() * 6) + 1;
                dieButton.setText(String.valueOf(newValue));
                dice[i] = new Die(newValue);
            } else {
                dice[i] = new Die(Integer.parseInt(dieButton.getText()));
            }
        }

        YatzyResultCalculator calculator = new YatzyResultCalculator(dice);
        // Opdater scorefelterne med de relevante værdier
        for (int i = 1; i < 7; i++) {
            if (!isScored[i - 1]) {
                int score = calculator.upperSectionScore(i);
                scoreFields[i - 1][0].setText(String.valueOf(score));
            }
        }
        if (!isScored[6]) scoreFields[6][0].setText(String.valueOf(calculator.upperSectionSum()));
        if (!isScored[7]) scoreFields[7][0].setText(String.valueOf(calculator.bonus()));
        if (!isScored[8]) scoreFields[8][0].setText(String.valueOf(calculator.onePairScore()));
        if (!isScored[9]) scoreFields[9][0].setText(String.valueOf(calculator.twoPairScore()));
        if (!isScored[10]) scoreFields[10][0].setText(String.valueOf(calculator.threeOfAKindScore()));
        if (!isScored[11]) scoreFields[11][0].setText(String.valueOf(calculator.fourOfAKindScore()));
        if (!isScored[12])scoreFields[12][0].setText(String.valueOf(calculator.largeStraightScore())); // Large straight
        if (!isScored[13])scoreFields[13][0].setText(String.valueOf(calculator.smallStraightScore())); // Small straight
        if (!isScored[14]) scoreFields[14][0].setText(String.valueOf(calculator.fullHouseScore())); // Full house
        if (!isScored[15])scoreFields[15][0].setText(String.valueOf(calculator.chanceScore())); // Chance
        if (!isScored[16])scoreFields[16][0].setText(String.valueOf(calculator.yatzyScore())); // Yatzy
    }

    public void chooseFieldAction(MouseEvent event) {
        TextField textField = (TextField) event.getSource();
        int categoryIndex = GridPane.getRowIndex(textField) - 1;

        if (!isScored[categoryIndex]) {
            isScored[categoryIndex] = true;
            lockScore(categoryIndex);
            textField.setDisable(true);

            updateTotalScores();
        }
    }

    public void lockScore(int categoryIndex) {
        if (categoryIndex >= 0 && categoryIndex < isScored.length) {
            isScored[categoryIndex] = true;
        }
    }

    private void updateTotalScores() {
        for (int j = 0; j < 2; j++) {
            int totalScore = 0;
            for (int i = 0; i < scoreFields.length - 1; i++) {
                TextField scoreField = scoreFields[i][j];
                if (isScored[i]) {
                    String text = scoreField.getText();
                    if (text.matches("\\d+")) { // Kun tal
                        totalScore += Integer.parseInt(text);
                    }
                }
                totalScoreLabels[j].setText(String.valueOf(totalScore));
            }
        }
    }
}