package dk.dtu.compute.se.pisd.designpatterns.observer;

import dk.dtu.compute.se.pisd.roborally.model.Board;

public class BoardDesignController {

    private int width, height;
    private Board board;
    private BoardDesignerView boardDesignerView;

    public BoardDesignController(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BoardDesignerView createView() {

        if (this.board == null) {
            this.createBoard();
        }

        this.boardDesignerView = new BoardDesignerView(this.board);

        return this.boardDesignerView;
    }

    public void createBoard() {
        this.board = new Board(this.width, this.height);
        this.board.setAntenna(null);
    }


}
