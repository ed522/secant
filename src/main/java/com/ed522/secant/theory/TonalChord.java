package com.ed522.secant.theory;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TonalChord {

    public record Chord(TonalChord chord, Rhythm rhythm) {}

    public enum PitchModificationType {
        /// A modification representing a scale degree that has been lowered
        /// by a semitone.
        FLATTENED,
        /// A modification representing a scale degree that has been raised
        /// by a semitone.
        RAISED,
        /// A modification representing a scale degree that will replace the
        /// third of this chord. If the third has been removed, this takes
        /// priority.
        SUSPENDED,
        /// A modification representing an added scale degree. The parameter
        /// is used as a scale degree to add to the root. To add non-diatonic
        /// tones, use `ADDED` as well as `FLATTENED`/`RAISED`.
        ADDED,
        /// A modification representing a scale degree that would normally be
        /// present, but has been removed. This also applies to
        REMOVED
    }

    public enum StructureModificationType {
        INVERSION, EXTENSION, SECONDARY, BORROWED_FROM
    }

    public record PitchModification(PitchModificationType type, int degree) {}

    public record StructureModification(StructureModificationType type,
                                        int parameter) {}

    @Unmodifiable
    private Pitch[] pitches;
    private final Pitch root;
    private final PitchModification[] pitchModifications;
    private final StructureModification[] structureModifications;
    private final Scale scale;

    public Pitch[] getPitches() {
        return pitches.clone();
    }
    public Pitch getRoot() {
        return root;
    }
    public PitchModification[] getPitchModifications() {
        return pitchModifications.clone();
    }
    public StructureModification[] getStructureModifications() {
        return structureModifications.clone();
    }
    public Scale getScale() {
        return scale;
    }

    /// Build the correct set of pitches. Modifications are applied in the
    /// following order:
    ///  1. Create a basic triad
    ///  2. Shift scale if using a secondary
    ///  3. Apply borrowed scales
    ///  4. Add extensions
    ///  5. Add missing flattened/raised degrees
    ///  6. Apply suspensions
    ///  7. Add and remove scale degrees
    ///  8. Flatten or raise
    ///  9. Apply inversion
    private void buildPitches() {
        // get an in-context scale
        Scale newScale;
        if (!scale.isDiatonic(root)) {
            newScale = scale.inMode(scale.diatonicDegreeOf(root));
        } else {
            // major by default, if it's a borrowed root then we can
            newScale = Scale.ofMajor(root);
        }
        // hashmap so that more than one degree cannot exist, and so that
        // it's easy to remove/replace degrees
        HashMap<Integer, Pitch> currentPitches = new HashMap<>();

        // make triad
        currentPitches.put(0, newScale.getDiatonic(0, root.octave()));
        currentPitches.put(2, newScale.getDiatonic(2, root.octave()));
        currentPitches.put(4, newScale.getDiatonic(4, root.octave()));

        // TODO: apply other modifications
        // (...)

        this.pitches = currentPitches.values().toArray(new Pitch[0]);

    }

    public TonalChord(Scale scale, Pitch root,
                      StructureModification[] structureModifications, PitchModification[] pitchModifications) {
        this.scale = scale;
        this.root = root;
        this.structureModifications = structureModifications;
        this.pitchModifications = pitchModifications;
        this.buildPitches();
    }

    /**
     * Constructs a basic triad based on the root.
     * @param scale
     * @param root
     */
    public TonalChord(Scale scale, Pitch root) {
        this(scale, root, new StructureModification[0], new PitchModification[0]);
    }

}
