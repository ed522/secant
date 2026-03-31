package com.ed522.secant.theory;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

/// Represents a set of allowable pitches to use for this
public class Scale {

    /// All of the diatonic notes in this scale.
    @Unmodifiable private final List<Integer> diatonicDegrees;
    /// The count of chromatic notes in this diatonic scale. In most Western
    /// music, this will be equal to 12.
    private final int chromaticNoteCount;
    /// The mode that this scale is currently in, relative to its "base"
    /// (ie. 4 for mixolydian, which is the 5th mode of the major scale)
    private final int currentMode;

    public Scale(int currentMode, int chromaticNoteCount,
                 Integer... diatonicDegrees) {
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = List.of(diatonicDegrees);
    }
    public Scale(int chromaticNoteCount, Integer... diatonicDegrees) {
        this(0, chromaticNoteCount, diatonicDegrees);
    }
    private Scale(int currentMode, int chromaticNoteCount,
                  List<Integer> diatonicDegrees) {
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = diatonicDegrees;
    }

    public int getChromaticNoteCount() {
        return this.chromaticNoteCount;
    }
    public int getDiatonicNoteCount() {
        return this.diatonicDegrees.size();
    }
    public int getCurrentMode() {
        return this.currentMode;
    }

    public Pitch getDiatonic(int degree, int octave) {
        return new Pitch(this.diatonicDegrees.get(degree), octave);
    }
    public Pitch getChromatic(int degree, int octave) {
        return new Pitch(this.diatonicDegrees.get(degree), octave);
    }

    public Scale inMode(int mode) {
        List<Integer> newDiatonicDegrees = new ArrayList<>();
        for (int i = 0; i < this.chromaticNoteCount; i++) {
            newDiatonicDegrees.add(i + mode,
                    this.diatonicDegrees.get(i) % this.chromaticNoteCount);
        }
        return new Scale(this.currentMode + mode,
                this.chromaticNoteCount, newDiatonicDegrees);
    }

    public int diatonicDegreeOf(Pitch pitch) {
        return this.diatonicDegrees.indexOf(pitch.pitchClass());
    }
    public int chromaticDegreeOf(Pitch pitch) {
        if (pitch.pitchClass() < this.chromaticNoteCount) {
            return pitch.pitchClass();
        }
        return -1;
    }

    public boolean isDiatonic(Pitch pitch) {
        return this.diatonicDegrees.contains(pitch.pitchClass());
    }
    public boolean isChromatic(Pitch pitch) {
        return this.chromaticNoteCount > pitch.pitchClass();
    }

}
