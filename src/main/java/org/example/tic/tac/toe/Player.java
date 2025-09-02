package org.example.tic.tac.toe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Player {
    private final String playerName;
    private final PlayingPiece playingPiece;
}
