package com.coolcompany.jtetris;

import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris {

	// Controls
	protected JCheckBox brainBox;
	protected JCheckBox animatedPlay;
	protected JSlider adversary;
	protected JLabel advLabel;

	protected DefaultBrain db = new DefaultBrain();

	/**
	 * Creates a new JBrainTetris using super class
	 */
	public JBrainTetris(int pixels) {
		super(pixels);
	}

	/**
	 * Creates a frame with JBraneTetris
	 */
	public static void main(String[] args) {
		JBrainTetris tetris = new JBrainTetris(16);
		JFrame frame = createFrame(tetris);
		frame.setVisible(true);
	}

	/**
	 * Create control panel using super class and add brain and adversary to it
	 */
	@Override
	public JComponent createControlPanel() {
		JPanel panel = (JPanel) super.createControlPanel();
		panel.remove(testButton);

		panel.add(new JLabel("Brain:"));
		brainBox = new JCheckBox("Brain active");
		brainBox.setSelected(false);
		animatedPlay = new JCheckBox("Animate falling");
		animatedPlay.setSelected(true);
		panel.add(brainBox);
		panel.add(animatedPlay);

		JPanel advPanel = new JPanel();
		advPanel.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0);
		adversary.setPreferredSize(new Dimension(100, 15));
		advPanel.add(adversary);
		advLabel = new JLabel("ok");
		advPanel.add(advLabel);
		panel.add(advPanel);


		return panel;
	}

	/**
	 * Use super class if adversary value is 0
	 * or random int 0-100 is less then adversary value,
	 * else use adversary to get new piece
	 */
	@Override
	public Piece pickNextPiece() {
		Piece newPiece;
		int value = adversary.getValue();
		int randValue = random.nextInt() % 100;
		if (value == 0 || randValue > value) {
			advLabel.setText("ok");
			newPiece = super.pickNextPiece();
		} else {
			advLabel.setText("*ok*");
			newPiece = pickAdversaryPiece();
		}
		return newPiece;
	}

	/*
	 * Calculate best move score for every piece and return one, which has
	 * the largest score
	 * If there is no best move for any piece, it means 
	 * that the game is going to be lost, so it has no meaning 
	 * which piece will be returned, in that case 
	 * return first piece from pieces array
	 */
	public Piece pickAdversaryPiece() {
		int pieceIndex = 0;
		double score = -1;
		Piece[] pieces = Piece.getPieces();
		Move curMove = new Move();
		for (int i = 0; i < pieces.length; i++) {
			curMove = db.bestMove(board, pieces[i], HEIGHT, curMove);
			if (curMove != null) {
				if (curMove.score > score) {
					score = curMove.score;
					pieceIndex = i;
				}
			}
		}
		return pieces[pieceIndex];
	}

	//	/**
//	 * Check if brain mode is selected, if it is, compute next position and rotation
//	 * using brain, else compute next position and rotation using super class
//	 */
	@Override
	public void computeNewPosition(int verb) {
		if (!brainBox.isSelected() || verb != DOWN) {
			super.computeNewPosition(verb);
		} else {
			newX = currentX;
			newY = currentY - 1;
			newPiece = currentPiece;
			Move move = new Move();
			move = db.bestMove(board, currentPiece, HEIGHT, move);
			if (move != null) {
				if (!move.piece.equals(currentPiece)) {
					newPiece = currentPiece.fastRotation();
					newX = newX + (currentPiece.getWidth() - newPiece.getWidth()) / 2;
					newY = newY + (currentPiece.getHeight() - newPiece.getHeight()) / 2;
				}
				if (move.x > currentX)
					newX++;
				else if (move.x < currentX)
					newX--;
				else if (!animatedPlay.isSelected() && move.piece.equals(currentPiece)) {
					computeNewPosition(DROP);
					// In case when piece is landed 
					// and next tick(DOWN) should cause PLACE_BAD
					if (newY == currentY)
						newY--;
				}
				if (board.place(newPiece, newX, newY) > Board.PLACE_ROW_FILLED) {
					newPiece = currentPiece;
					newX = currentX;
				}
				board.undo();
			}
		}
	}

}
