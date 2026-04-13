package com.ed522.secant.theory;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

/// Represents a set of pitches for use in representing music. `ScaleSet` is
/// what gives meaning to the 'pitch class' number in `Pitch`
public class ScaleSet {

    @Unmodifiable private static final Integer[] MAJOR_DEGREES =
            new Integer[] {0, 2, 4, 5, 7, 9, 11};

    /// Create a major scale based on the given root.
    ///
    /// @return A newly-allocated major scale based on the given pitch
    public static ScaleSet ofMajor(Pitch root) {
        return new ScaleSet(root, 0, 12, MAJOR_DEGREES);
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

    /// Create a scale that has the given tonic, number of consecutive chromatic
    /// degrees and diatonic degrees. It is assumed to be the zeroth
    /// (traditionally first) mode of its particular scale
    public ScaleSet(Pitch tonic, int chromaticNoteCount, Integer... diatonicDegrees) {
        this(tonic, 0, chromaticNoteCount, diatonicDegrees);
    }

    /// Creates a scale that has the given tonic, number of consecutive
    /// chromatic degrees, diatonic degrees and current mode (0-based).
    public ScaleSet(Pitch tonic, int currentMode, int chromaticNoteCount,
                    Integer... diatonicDegrees) {
        this.tonic = tonic;
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = List.of(diatonicDegrees);
        this.octaveBreak = this.findOctaveBreak();
    }

    /// Creates a scale (as above) but with a preallocated list of degrees.
    private ScaleSet(Pitch tonic, int currentMode, int chromaticNoteCount,
                     List<Integer> diatonicDegrees) {
        this.tonic = tonic;
        this.currentMode = currentMode;
        this.chromaticNoteCount = chromaticNoteCount;
        this.diatonicDegrees = diatonicDegrees; // unmodified but uncopied
        this.octaveBreak = this.findOctaveBreak();
    }

    /// Get the count of consecutive chromatic notes in this scale. In most
    /// Western music this will be 12.
    ///
    /// @return the total count of chromatic degrees - i.e. in 12TET, returns 12
    public int getChromaticNoteCount() {
        return this.chromaticNoteCount;
    }

    /// Gets the 'current mode' of this scale -- its mode relative to some base
    /// mode of the scale.
    ///
    /// @return the current mode of the scale
    public int getCurrentMode() {
        return this.currentMode;
    }

    /// Get the amount of diatonic notes that this scale holds.
    ///
    /// @return the total count of valid diatonic degrees
    public int getDiatonicNoteCount() {
        return this.diatonicDegrees.size();
    }

    /// Get the `Pitch` that is the tonic of this scale
    ///
    /// @return the tonic of this scale
    public Pitch getTonic() {
        return this.tonic;
    }

    /// Create a new `Pitch` representing the given chromatic degree of the
    /// scale, shifted by some number of octaves. If the degree overflows an
    /// octave, it will be shifted up an additional octave.
    ///
    /// @param degree      The chromatic scale degree of the desired pitch (any
    ///                    integer, but most commonly less than the total
    ///                    chromatic note count)
    /// @param octaveShift The amount of octaves to shift the pitch relative to
    ///                    the tonic (any integer)
    /// @return A new `Pitch` representing the given chromatic degree
    public Pitch forChromatic(int degree, int octaveShift) {
        int pitchClass = (degree + this.tonic.pitchClass()) % chromaticNoteCount;
        int extraOctaves = (int) Math.floor((double) degree / chromaticNoteCount);

        if (degree < this.tonic.pitchClass()) {
            // next octave from the tonic
            extraOctaves++;
        }

        return new Pitch(this.tonic.octave() + octaveShift + extraOctaves, pitchClass);
    }

    /// Create a new `Pitch` representing the given diatonic degree of the
    /// scale, shifted by some number of octaves. If the degree overflows an
    /// octave, it will be shifted up an additional octave.
    ///
    /// @param degree      The diatonic scale degree of the desired pitch (any
    ///                    integer, but most commonly less or equal to the
    ///                    highest diatonic degree)
    /// @param octaveShift The amount of octaves to shift the pitch relative to
    ///                    the tonic (any integer)
    /// @return A new `Pitch` representing the given degree
    public Pitch forDiatonic(int degree, int octaveShift) {
        int rawPitchClass =
                this.diatonicDegrees.get(degree % this.getDiatonicNoteCount()) + tonic.pitchClass();
        int extraOctaves = degree / this.getDiatonicNoteCount() + rawPitchClass / chromaticNoteCount;

        return new Pitch(this.tonic.octave() + octaveShift + extraOctaves,
                rawPitchClass % chromaticNoteCount);
    }

    /// Create a new `Scale` similar to this one, but shifted by `n` diatonic
    /// degrees in either direction. An `n` parameter of `0` is the identity -
    /// it will effectively shallow-clone this object.
    ///
    /// @param n The amount of degrees to shift this scale by
    /// @return A new scale, shifted by `n` degrees
    public ScaleSet modeShift(int n) {
        Integer[] diatonicDegreesShifted =
                new Integer[this.diatonicDegrees.size()];

        for (int i = 0; i < this.diatonicDegrees.size(); i++) {
            diatonicDegreesShifted[i] =
                    (this.diatonicDegrees.get(i) + n) % this.chromaticNoteCount;
        }

        return new ScaleSet(this.forDiatonic(n, this.tonic.octave()),
                this.currentMode + n, this.chromaticNoteCount,
                List.of(diatonicDegreesShifted));
    }

    /// Create a new `Scale` that is similar to this one, but in the given mode
    /// of its base scale. This takes into account `currentMode`.
    ///
    /// @param mode The mode to create the new scale in, relative to the base
    ///             scale of this instance
    /// @return A new scale in the given mode
    public ScaleSet inMode(int mode) {
        return this.modeShift(mode - this.currentMode);
    }

    /// Create a new scale in the same mode as this one, but on a different
    /// tonic.
    ///
    /// @return a new scale based on the specified tonic
    public ScaleSet onTonic(Pitch tonic) {
        return new ScaleSet(tonic, this.currentMode, this.chromaticNoteCount,
                this.diatonicDegrees);
    }

    /// Get the diatonic degree of the given pitch. Pitch classes over the
    /// number of chromatic degrees are mapped onto higher octaves.
    public int diatonicDegreeOf(Pitch pitch) {
        return this.diatonicDegrees.indexOf(chromaticDegreeOf(pitch));
    }

    public int chromaticDegreeOf(Pitch pitch) {
        return (pitch.pitchClass() - tonic.pitchClass()) % chromaticNoteCount;
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
