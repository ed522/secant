package com.ed522.secant.theory;

import org.jetbrains.annotations.NotNull;

/// Represents a single, abstract note with no duration.
/// @param octave           The octave of this note, where 440hz rests on A4
/// @param pitchClass       The pitch class (or letter name) within its octave
public record Pitch(int octave, int pitchClass) {
    // public int compareTo(@NotNull Pitch other, @NotNull Scale scale) {
    //     int scaleDegrees = scale.getChromaticNoteCount();
    // }
}
