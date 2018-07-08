package com.rashtell.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static int messengerLinkedListIndex = 0;
    private static int gameNumber = 0;
    private static int firstToPlay = 1;

    protected Button row1col1Button, row1col2Button, row1col3Button, row2col1Button, row2col2Button, row2col3Button, row3col1Button, row3col2Button, row3col3Button;
    protected RadioButton singlePlayerRadioButton, multiPlayerRadioButton, playXRadioButton, playORadioButton;
    protected TextView player;
    protected View player1View;
    private LinkedList availableMoves;
    private LinkedList storeMovesLinkedList;
    private LinkedList messengerLinkedList;
    private LinkedList tempLinkedList;
    private String flagWinner, firstPlayer;
    private int turn, xScore, oScore, winnerInitPosition, initPos;
    private boolean isGameOver, trackPlayer;
    private boolean newSession;

    public MainActivity() {
    }

    public static void writeString(Context context, final String KEY, String property) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SyncStateContract.Constants.ACCOUNT_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY, property);
        editor.commit();
    }

    public static String readString(Context context, final String KEY) {
        return context.getSharedPreferences(SyncStateContract.Constants.ACCOUNT_NAME, MODE_PRIVATE).getString(KEY, null);
    }

    /**
     * Init method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getButtons();
        storeIds();

        player = findViewById(R.id.player);
        player.setText("You are X");

        xScore = 0;
        oScore = 0;
        initPos = 0;
        turn = 1;
        trackPlayer = false;
        isGameOver = false;

        storeMovesLinkedList = new LinkedList();
        availableMoves = new LinkedList();
        messengerLinkedList = new LinkedList();
        firstPlayer = "";
        newSession = true;

//        if (!readString(this, "Stored Moves").equals(null)) {
//
//            Object object = readString(this, "Stored Moves");
//            storeMovesLinkedList = (LinkedList) object;
//        }

    }

    private void getButtons() {
        row1col1Button = findViewById(R.id.row1col1);
        row1col2Button = findViewById(R.id.row1col2);
        row1col3Button = findViewById(R.id.row1col3);
        row2col1Button = findViewById(R.id.row2col1);
        row2col2Button = findViewById(R.id.row2col2);
        row2col3Button = findViewById(R.id.row2col3);
        row3col1Button = findViewById(R.id.row3col1);
        row3col2Button = findViewById(R.id.row3col2);
        row3col3Button = findViewById(R.id.row3col3);

        singlePlayerRadioButton = findViewById(R.id.singlePlayerRadioButton);
        multiPlayerRadioButton = findViewById(R.id.multiPlayerRadioButton);
        playXRadioButton = findViewById(R.id.playXRadioButton);
        playORadioButton = findViewById(R.id.playORadioButton);
    }

    /**
     * Disables the game character options for multi player state
     *
     * @param view
     */
    public void playerModeSelected(View view) {

        if (view.getId() == singlePlayerRadioButton.getId()) {
            playORadioButton.setEnabled(true);
            playXRadioButton.setEnabled(true);
        } else if (view.getId() == multiPlayerRadioButton.getId()) {
            playORadioButton.setEnabled(false);
            playXRadioButton.setEnabled(false);
        }
    }

    /**
     * This method is invoked by the Play X and Play O radio button
     * It increments the variable turn
     * The variable turn is use by the selectCharacter() method to determine
     * the character option chose by the gamer
     * Sets the header text to show the selected game character
     *
     * @param view
     */
    public void selectGameCharacter(View view) {
        if (playXRadioButton.isChecked()) {
            turn = 0;

            player.setText("You are X");

        } else if (playORadioButton.isChecked()) {
            turn = 1;

            player.setText("You are O");
        }
    }

    /**
     * Specifies the game character to be used as selected by the gamer
     *
     * @return
     */

    private String selectCharacter() {
//playXRadioButton and playORadioButton as been taken care of in selectGameCharacter onClick method

        String xORo = "E";

        if (turn % 2 == 1) {
            xORo = "X";
        } else if (turn % 2 == 0) {
            xORo = "O";
        }
        ++turn;
        return xORo;
    }

    /**
     * This method is invoked by the game buttons
     *
     * @param view
     */
    public void buttonClicked(View view) {
        player1View = view;
        trackPlayer = true;
        playGame(view);
    }

    private void buttonClickedAction(View view) {
        setClickable("radio", false);
        String xORo = selectCharacter();

        if (!isGameOver) {
            int buttonClickedId = view.getId();
            Button buttonClicked = findViewById(buttonClickedId);
            buttonClicked.setText(xORo);
            buttonClicked.setClickable(false);
            if (newSession) {
                firstPlayer = buttonClicked.getText().toString();
                //Tracks human player progress
            }
            getMoves(buttonClickedId);
            if (newSession) newSession = false;
        }
    }

    private void machineStudent() {

//        Button buttonClicked = findViewById(artificialIntelligence());
//        if (singlePlayerRadioButton.isChecked() &&
//                playXRadioButton.isChecked() && this.turn == 0 && !isGameOver) {
//            nextButton.setText("O");
//            ++turn;
//
//        } else if (singlePlayerRadioButton.isChecked() &&
//                playORadioButton.isChecked() && this.turn == 1 && !isGameOver) {
//            nextButton.setText("X");
//            ++turn;
//        }

        int suggestedMove = artificialIntelligence();
        Button nextButton = findViewById(suggestedMove);
        if (!isGameOver) {
            setClickable("radio", false);
            String xORo = selectCharacter();
            nextButton.setText(xORo);
            nextButton.setClickable(false);
            if (newSession) firstPlayer = nextButton.getText().toString();
            getMoves(nextButton.getId());

            if (newSession) newSession = false;
        }

    }

    /**
     * This method enables or disables the buttons and radio buttons
     *
     * @param type
     * @param bool
     */
    private void setClickable(String type, boolean bool) {
        if (type.equals("radio")) {
            playXRadioButton.setClickable(bool);
            playORadioButton.setClickable(bool);
            singlePlayerRadioButton.setClickable(bool);
            multiPlayerRadioButton.setClickable(bool);
        }

        if (type.equals("button")) {
            row1col1Button.setClickable(bool);
            row1col2Button.setClickable(bool);
            row1col3Button.setClickable(bool);
            row2col1Button.setClickable(bool);
            row2col2Button.setClickable(bool);
            row2col3Button.setClickable(bool);
            row3col1Button.setClickable(bool);
            row3col2Button.setClickable(bool);
            row3col3Button.setClickable(bool);
        }
    }

    /**
     * Sets the rules for wining
     * Flags for gameover
     *
     * @return
     */
    private boolean rules() {
        Boolean isWin = false;
        String r1c1 = row1col1Button.getText().toString(), r1c2 = row1col2Button.getText().toString(), r1c3 = row1col3Button.getText().toString(), r2c1 = row2col1Button.getText().toString(), r2c2 = row2col2Button.getText().toString(), r2c3 = row2col3Button.getText().toString(), r3c1 = row3col1Button.getText().toString(), r3c2 = row3col2Button.getText().toString(), r3c3 = row3col3Button.getText().toString();

        String winner = "";

        if (r1c1.equals(r1c2) && r1c2.equals(r1c3) && !r1c1.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c1;
        } else if (r2c1.equals(r2c2) && r2c2.equals(r2c3) && !r2c1.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r2c1;
        } else if (r3c1.equals(r3c2) && r3c2.equals(r3c3) && !r3c1.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r3c1;
        } else if (r1c1.equals(r2c1) && r2c1.equals(r3c1) && !r1c1.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c1;
        } else if (r1c2.equals(r2c2) && r2c2.equals(r3c2) && !r1c2.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c2;
        } else if (r1c3.equals(r2c3) && r2c3.equals(r3c3) && !r1c3.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c3;
        } else if (r1c1.equals(r2c2) && r2c2.equals(r3c3) && !r1c1.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c1;
        } else if (r1c3.equals(r2c2) && r2c2.equals(r3c1) && !r1c3.isEmpty()) {
            isWin = true;
            isGameOver = true;
            winner = r1c3;
        } else isGameOver = !r1c1.isEmpty() && !r1c2.isEmpty() && !r1c3.isEmpty()
                && !r2c1.isEmpty() && !r2c2.isEmpty() && !r2c3.isEmpty()
                && !r3c1.isEmpty() && !r3c2.isEmpty() && !r3c3.isEmpty();

        if (isWin) {
            flagWinner = winner;
            updateScores(winner);
            setClickable("button", false);
            storeMoves();

        }

        return isWin;
    }

    /**
     * Updates the scores
     *
     * @param winner
     */
    private void updateScores(String winner) {
        TextView scoreTextView = findViewById(R.id.scoreTextView);

        if (winner.equals("X")) {
            ++xScore;
        } else if (winner.equals("O")) {
            ++oScore;
        }

        String scoreMessage = "messi (X) " + xScore + " : " + oScore + " ronaldo (O)";
        scoreTextView.setText(scoreMessage);
    }

    /**
     * This method is invoked by the RESET button
     * It invokes the restartGame() helper method
     *
     * @param view
     */
    public void restartClicked(View view) {
        restartGame();
    }

    /**
     * This method restarts the current game
     */
    private boolean restartGame() {
        newSession = true;
        setEmptyText("Button");
        setClickable("button", true);
        turnHandler();
        isGameOver = false;
        return true;
    }

    private void turnHandler() {
        if (!isGameOver) {
            if (firstPlayer.equals("X") && playXRadioButton.isChecked()) turn = 1;
            else if (firstPlayer.equals("O") && playORadioButton.isChecked()) turn = 0;
            else if (firstPlayer.equals("X")) {
                turn = 1;
                machineStudent();
            } else if (firstPlayer.equals("O")) {
                turn = 0;
                machineStudent();
            }
        } else {
            if (firstPlayer.equals("X") && playXRadioButton.isChecked()) {
                turn = 0;
                newSession = true;
                machineStudent();
            } else if (firstPlayer.equals("O") && playORadioButton.isChecked()) {
                turn = 1;
                newSession = true;
                machineStudent();
            } else if (firstPlayer.equals("X")) {
                newSession = true;
                turn = 0;
            } else if (firstPlayer.equals("O")) {
                turn = 1;
                newSession = true;
            }

        }
    }

    /**
     * This method is invoked by the RESET button
     * It invokes the restartGame() helper method
     * It invokes the resetGame() helper method
     *
     * @param view
     */
    public void resetClicked(View view) {
        resetGame();
    }

    /**
     * This method handles all the reset operations
     * It invokes the setEmptyText() method on the necessary components
     */
    private void resetGame() {
        restartGame();
        setClickable("radio", true);

        oScore = 0;
        xScore = 0;
        updateScores("");
    }

    /**
     * Empties the text value of buttons
     */
    private void setEmptyText(String componet) {
        if (componet.equals("Button")) {

            row1col1Button.setText("");
            row1col2Button.setText("");
            row1col3Button.setText("");
            row2col1Button.setText("");
            row2col2Button.setText("");
            row2col3Button.setText("");
            row3col1Button.setText("");
            row3col2Button.setText("");
            row3col3Button.setText("");
        }
    }

    /**
     * Shows win or draw toast
     *
     * @param isWin
     */
    private void showToast(Boolean isWin) {
        Toast showWinToast, showDrawToast;

        showWinToast = Toast.makeText(this, "You Won"
                , Toast.LENGTH_LONG);
        showDrawToast = Toast.makeText(this, "Draw"
                , Toast.LENGTH_LONG);

        if (isWin) {
            showWinToast.show();
        } else if (!isWin && isGameOver) {
            showDrawToast.show();
        }
    }

    private void storeIds() {
        Integer r1c1 = row1col1Button.getId(), r1c2 = row1col2Button.getId(), r1c3 = row1col3Button.getId(), r2c1 = row2col1Button.getId(), r2c2 = row2col2Button.getId(), r2c3 = row2col3Button.getId(), r3c1 = row3col1Button.getId(), r3c2 = row3col2Button.getId(), r3c3 = row3col3Button.getId();
        //store all the gameButtons index in an array
        Integer[] rcId = {r1c1, r1c2, r1c3, r2c1, r2c2, r2c3, r3c1, r3c2, r3c3};
//        Integer[] rcId = storeIds();
        //If its a new game session, store all the the gameButtons index to the availableMoves list
        // The newSession is true by default and the gameButton click event sets it as false
        // The newSession is set back to true when the restart() is invoked

        //Clears the availableMoves list when the game is over
        if (newSession) {
            availableMoves.clear();
            for (int x = 0; x < rcId.length; x++) {
                availableMoves.add(rcId[x]);
            }
        }
    }

    /**
     * Stores all game moves in storeMovesLinkedList
     * Returns all the available moves as an LinkedList
     *
     * @param move
     * @return
     */
    private void getMoves(int move) {
        storeIds();

        //Remove the index of gameButton id that has been clicked
        if (!isGameOver) availableMoves.remove((Object) move);
/**
 *
 */
        //Temporarily stores all the moves for the session in the messengerLinkedList
        if (!isGameOver) messengerLinkedList.add(messengerLinkedListIndex, move);
        ++messengerLinkedListIndex;

//        storeMoves();
    }

    private void storeMoves() {
        //Adds the winner of the session to the end of the list
        //Resets the messengerLinkedListIndex to zero for the new session
        if (isGameOver) {
            //To know if the winner was the first or second player
            //This will help us accurately know the winners game pattern storage
            if (firstPlayer.equals(flagWinner)) {
                //The first player is the winner
                winnerInitPosition = 0;
                //The Second player is the winner
            } else winnerInitPosition = 1;

            //Adds the winnersInitPosition to th messengerLinkedList
            messengerLinkedList.addFirst(winnerInitPosition);
//            messengerLinkedList.addLast(flagWinner);
            messengerLinkedListIndex = 0;
        }
        //Creates a temporary LinkedList at the end of the session
        if (isGameOver) {
            tempLinkedList = new LinkedList();

            //Adds each object in the messengerLinkedList to the newly created tempLinkedList
            for (Object messenger : messengerLinkedList) {
                tempLinkedList.add(messenger);
            }

            //Clears the messengerLinkedList and prepares it for the new session
            messengerLinkedList.clear();

            //Adds the tempLinkedList as an object to the storeMovesLinkedList
            storeMovesLinkedList.add(gameNumber, tempLinkedList);
            writeString(this, "Stored Moves", storeMovesLinkedList.toString());
            ++gameNumber;
        }
    }

    /**
     * Contains instruction on how to select program's next move
     * Uses users data if available
     * Else uses some specified sets of instructions
     *
     * @return
     */
    private int artificialIntelligence() {
        LinkedList choices = new LinkedList();
        //Machine thinking from past experience
        if (!isGameOver) {
            ArrayList returnArrayList;
            if (!storeMovesLinkedList.isEmpty()) {
                //Iterates through storeMovesLinkedList which is the master branch
                for (int x = 0; x < storeMovesLinkedList.size(); x++) {
                    //Adapts the stored object back to Linked List
                    LinkedList helperLinkedList = (LinkedList) storeMovesLinkedList.get(x);
                    if (newSession) {
                        initPos = Integer.parseInt(helperLinkedList.getFirst().toString());
                        //Removes the initial position from the helperLinkedList
                        helperLinkedList.removeFirst();
                    }
                    //Creates a new LinkedList called winnerMoves
                    LinkedList winnerMoves = new LinkedList();
                    //Gets the size of the helperLinkedList
                    int helperLinkedListSize = helperLinkedList.size();
                    //Extracts just the winners move data
                    if (initPos == 0) {
                        for (int y = 0; y < helperLinkedListSize; y = y + 2) {
                            winnerMoves.add(helperLinkedList.get(y));
                        }
                    } else if (initPos == 1) {
                        for (int y = 1; y < helperLinkedListSize; y = y + 2) {
                            winnerMoves.add(helperLinkedList.get(y));
                        }
                    }
                    storeIds();
                    //Compares the two LinkedList index by index and returns the number of matches
                    ArrayList payLoad = compare(winnerMoves, availableMoves);
                    //Stores the number of matches found at the same index int each dataSet
                    int numOfMatches = Integer.parseInt(payLoad.get(1).toString());
                    //Stores the position(s) at which matches were found for the current iteration
                    returnArrayList = (ArrayList) payLoad.get(0);
                    //Adds each LinkedList which was previously compared
                    // , to a new ArrayList called choices
                    //The LinkedList is added at the index numOfMatches
                    if (choices.size() < numOfMatches)
                        for (int z = 0; z < numOfMatches; z++) {
                            choices.add(new LinkedList<>());
                        }
                    else if (choices.size() >= numOfMatches) return naturalIntelligence();
                    //Adds the returnArrayList to the choices an index
                    // corresponding to the number of matches found
                    choices.add(numOfMatches, returnArrayList);

                }

                //Creates a new ArrayList to store the ArrayList in last index of choices
                ArrayList bestChoiceArrayList = (ArrayList) choices.getLast();

                //Stores the first move in the bestChoiceArrayList
                int moveToBePlayedIndex = Integer.parseInt(bestChoiceArrayList.get(0).toString());

                //Gets the move from availableMoves at index moveToBePlayedIndex
                int moveToBePlayed = Integer.parseInt(availableMoves.get(moveToBePlayedIndex).toString());

                //Returns the move
                return moveToBePlayed;

            } else {
                return naturalIntelligence();
            }
        }
        return naturalIntelligence();
    }

    private int naturalIntelligence() {
        int moveToBePlayedIndex = 0;
//               Todo moveToBePlayed = availableMoves.size();
        int moveToBePlayed = Integer.parseInt(availableMoves.getFirst().toString());
        availableMoves.remove(0);
        return moveToBePlayed;
    }

    private ArrayList compare(LinkedList winnerMoves, LinkedList availableMoves) {
        int numOfMatches = 0;
        ArrayList posIndexArray = new ArrayList<Integer>();

        int availableMovesSize = availableMoves.size();
        int winnerMovesSize = winnerMoves.size();
        int minSize;


        if (winnerMovesSize <= availableMovesSize) minSize = winnerMovesSize;
        else minSize = availableMovesSize;

        for (int x = 0; x < minSize; x++) {
            for (int y = 0; y < availableMovesSize; y++) {
                if (winnerMoves.get(x).equals(availableMoves.get(y))) {
                    posIndexArray.add(y);
                    ++numOfMatches;
                    break;
                }
            }
        }

        ArrayList returnArrayList = new ArrayList();
        returnArrayList.add(posIndexArray);
        returnArrayList.add(numOfMatches);

        return returnArrayList;
    }

    private void playGame(View view) {
        Boolean rule;
        rule = rules();
        if (firstToPlay % 2 == 1 && !isGameOver && trackPlayer == true) {
            buttonClickedAction(view);
            ++firstToPlay;
        }
        if (rule == false) rule = rules();
        if (firstToPlay % 2 == 0 && !isGameOver) {
            machineStudent();
            ++firstToPlay;
            trackPlayer = false;
        }

        if (rule == false) rule = rules();
        showToast(rule);
    }

    public void continueClicked(View view) {
        newSession = true;
        //Gets the character of the first player and stores it in firstPlayer
        if (newSession) ++turn;
        turnHandler();
        isGameOver = false;
        setEmptyText("Button");
        setClickable("button", true);

    }
}