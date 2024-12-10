// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

// public class GameController {
//     private GameState gameState;
//     private GameView gameView;
//     private SpellsSelector spellsSelector;

//     public GameController(GameState gameState, GameView gameView, SpellsSelector spellsSelector) {
//         this.gameState = gameState;
//         this.gameView = gameView;
//         this.spellsSelector = spellsSelector;

//         setupListeners();
//         updateUI();
//     }

//     private void setupListeners() {
//         gameView.getBtnAuto().addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 spellsSelector.optimize(gameView.getSpellTableModel(), gameState.getMana(), gameState.getCurrentBoss().getBossMaxHp());
//                 updateUI();
//             }
//         });

//         gameView.getBtnCastSpells().addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 castSpells();
//             }
//         });
//     }

//     private void castSpells() {
//         // Logic for casting spells
//         // Update game state and UI accordingly
//         updateUI();
//     }

//     private void updateUI() {
//         gameView.updateManaLabel(0, gameState.getMana()); // Example values
//         gameView.updateExpectedDMGLabel(0); // Example values
//     }
// }