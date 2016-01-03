package org.misja.stockfighter;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeuristicsTest {
    private static final double EPSILON = 0.005;

    @Test
    public void positionRiskShouldBeZeroForFlatPosition() {
        assertEquals(0, Heuristics.positionRisk(0, 50), EPSILON);
    }

    @Test
    public void positionRiskShouldBeOneForExtremePositions() {
        assertEquals(1, Heuristics.positionRisk(1000, 50), EPSILON);
        assertEquals(1, Heuristics.positionRisk(-1000, 50), EPSILON);
    }

    @Test
    public void positionRiskShouldInitiallyGrowSlowlyButThenFaster() {
        double risk100 = Heuristics.positionRisk(100, 50);
        double risk300 = Heuristics.positionRisk(300, 50);
        double risk700 = Heuristics.positionRisk(700, 50);
        double risk900 = Heuristics.positionRisk(900, 50);
        assertTrue((risk300 - risk100) * 2 < risk900 - risk700);
    }
}