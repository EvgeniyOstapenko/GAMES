package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public interface BoardEvaluator {

    int evaluate (Board board, int depth);
}
