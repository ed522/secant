package com.ed522.secant.theory;

/// Represents the temporal component of a single musical note.
///
/// @param value            The base length for this note, i.e. 4 for a quarter
///                         note. 1 is equal to a whole note, and 0 and below
///                         refer to multiples of a whole note (0 maps to a
///                         breve, -1 to a longa etc)
/// @param multiplier       How long this note is relative to the value, i.e. 3
///                         with a divisor of 4 would yield a 3-beat-long note
///                         in 4/4
/// @param barOffset        How far into this piece the note appears, in bars
/// @param offsetValue      A value for its offset within a bar, in the same
///                         terms as its regular value
/// @param offsetMultiplier A multiplier for its value offset inside a bar,
///                         acting in the same way as its regular multiplier
public record Rhythm(int multiplier, int value, int barOffset,
                     int offsetValue, int offsetMultiplier) {}
