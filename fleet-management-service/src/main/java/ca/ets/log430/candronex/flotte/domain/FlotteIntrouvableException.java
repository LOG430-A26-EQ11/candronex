package ca.ets.log430.candronex.flotte.domain;

import ca.ets.log430.candronex.commun.domaine.RessourceIntrouvableException;

public class FlotteIntrouvableException extends RessourceIntrouvableException {
    public FlotteIntrouvableException() {
        super("FLOTTE_INTROUVABLE", "Flotte introuvable.");
    }
}