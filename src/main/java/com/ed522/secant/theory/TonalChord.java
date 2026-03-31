package com.ed522.secant.theory;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.List;

public class TonalChord {

    public record Chord(TonalChord chord, Rhythm rhythm) {}

    public enum PitchModificationType {
        FLATTENED, RAISED, SUSPENDED,
        ADDED, REMOVED
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
    ///  2. Apply secondary modifications
    ///  3. Apply borrowed scales
    ///  4. Add extensions
    ///  5. Flatten or raise, adding if necessary
    ///  6. Add and remove scale degrees
    ///  7. Apply suspensions
    ///  8. Apply inversion
    private void buildPitches() {
        // find the degree of the root
        int rootDegree = scale.diatonicDegreeOf(root);

        // get an in-context scale
        Scale newScale;
        if (!scale.isDiatonic(root)) {
            newScale = scale.inMode(rootDegree);
        } else {

        }

        // make triad
        List<Pitch> currentPitches = new ArrayList<>();
        currentPitches.add(newScale.getDiatonic(0, root.octave()));
        currentPitches.add(newScale.getDiatonic(2, root.octave()));
        currentPitches.add(newScale.getDiatonic(4, root.octave()));

        // TODO: apply other modifications
        // (...)

        this.pitches = currentPitches.toArray(new Pitch[0]);

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
