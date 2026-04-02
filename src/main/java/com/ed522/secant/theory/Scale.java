package com.ed522.secant.theory;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// Represents a set of pitches for use in representing music. `Scale`
/// is what gives meaning to the 'pitch class' number in `Pitch`
public class Scale {

    /// Create a major scale based on the given root.
    ///
    /// @return A newly-allocated major scale based on the given pitch
    public static Scale ofMajor(Pitch root) {
        Integer[] degrees = new Integer[] {0, 2, 4, 5, 7, 9, 11};
        for (int i = 0; i < degrees.length; i++) {
            degrees[i] = (degrees[i] + root.pitchClass()) % 12;
        }
        return new Scale(root, 0, 12, degrees);
    }
    /// All of the diatonic notes in this scale.
    @Unmodifiable private final List<Integer> diatonicDegrees;
    /// The count of chromatic notes in this diatonic scale. In most Western
    /// music, this will be equal to 12.
    private final int chromaticNoteCount;
    /// The mode that this scale is currently in, relative to its "base" (ie. 4
    /// for mixolydian, which is the 5th mode of the major scale)
    private final int currentMode;
    /// The degree of the note that starts any given octave (pitch class 0).
    private final int octaveBreak;
    /// The tonic of this scale.
    private final Pitch tonic;

    public Scale(Pitch tonic, int chromaticNoteCount, Integer... diatonicDegrees) {
        this(tonic, 0, chromaticNoteCount, diatonicDegrees);
    }

    public Scale(Pitch tonic, int currentMode, int chromaticNoteCount,
                 Integer... diatonicDegrees) {
        this.tonic = tonic;
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = List.of(diatonicDegrees);
        this.octaveBreak = this.findOctaveBreak();
    }

    private Scale(Pitch tonic, int currentMode, int chromaticNoteCount,
                  List<Integer> diatonicDegrees) {
        this.tonic = tonic;
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = diatonicDegrees;
        this.octaveBreak = this.findOctaveBreak();
    }

    public int getChromaticNoteCount() {
        return this.chromaticNoteCount;
    }

    public int getCurrentMode() {
        return this.currentMode;
    }

    public int getDiatonicNoteCount() {
        return this.diatonicDegrees.size();
    }

    public Pitch getChromatic(int degree, int octaveShift) {
        int normalizedDegree = ((degree % chromaticNoteCount) + chromaticNoteCount) % chromaticNoteCount;
        int extraOctaves = (int) Math.floor((double) degree / chromaticNoteCount);

        return new Pitch(this.tonic.octave() + octaveShift + extraOctaves, normalizedDegree);
    }

    public Scale inMode(int mode) {
        List<Integer> newDiatonicDegrees = new ArrayList<>();
        int noteCount = getDiatonicNoteCount();
        for (int i = 0; i < noteCount; i++) {
            newDiatonicDegrees.add(this.diatonicDegrees.get((i + mode) % noteCount));
        }

        Pitch newTonic = getDiatonic(mode, 0);
        return new Scale(newTonic, this.currentMode + mode,
                this.chromaticNoteCount, newDiatonicDegrees);
    }

    public Pitch getDiatonic(int degree, int octaveShift) {
        int noteCount = getDiatonicNoteCount();
        int normalizedDegree = ((degree % noteCount) + noteCount) % noteCount;
        int extraOctaves = (int) Math.floor((double) degree / noteCount);

        return new Pitch(this.tonic.octave() + octaveShift + extraOctaves, this.diatonicDegrees.get(normalizedDegree));
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

    private int findOctaveBreak() {
        return Collections.min(this.diatonicDegrees);
    }

}
