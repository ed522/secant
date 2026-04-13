package com.ed522.secant.theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NoteCanvas {

    private final List<Note> notes;
    private final List<TonalChord.Chord> chords;
    private final List<Consumer<Note>> onAddListeners;
    private final List<Consumer<Note>> onRemoveListeners;
    private final ScaleSet scale;

    public NoteCanvas(final ScaleSet scale) {
        this.scale = scale;
        this.chords = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.onAddListeners = new ArrayList<>();
        this.onRemoveListeners = new ArrayList<>();
    }

    public ScaleSet getScale() {
        return this.scale;
    }

    public void listenOnAdd(Consumer<Note> listener) {
        this.onAddListeners.add(listener);
    }
    public void listenOnRemove(Consumer<Note> listener) {
        this.onRemoveListeners.add(listener);
    }

    public void addNote(Note note) {
        this.notes.add(note);
        this.onAddListeners.forEach(listener -> listener.accept(note));
    }
    public void removeNote(Note note) {
        this.notes.removeIf(n -> n.pitch().equals(note.pitch()) &&
                n.rhythm().isAtSamePosition(note.rhythm()));
        this.onRemoveListeners.forEach(listener -> listener.accept(note));
    }

    public boolean hasNote(Note note) {
        return this.notes.stream().anyMatch(n -> n.pitch().equals(note.pitch()) &&
                n.rhythm().isAtSamePosition(note.rhythm()));
    }

    public void addChord(TonalChord.Chord chord) {
        this.chords.add(chord);
        for (Pitch p : chord.chord().getPitches()) {
            this.onAddListeners.forEach(
                    listener -> listener.accept(new Note(chord.rhythm(), p))
            );
        }
    }
    public void removeChord(TonalChord.Chord chord) {
        this.chords.remove(chord);

        for (Pitch p : chord.chord().getPitches()) {
            this.onRemoveListeners.forEach(
                    listener -> listener.accept(new Note(chord.rhythm(), p))
            );
        }
    }

    public Note[] getNotes() {
        // get every chord, then convert it into a list of notes (using the
        // chord's rhythm)
        List<Note> chordNotes =
                this.chords.stream().flatMap(c ->
                            Arrays.stream(c.chord().getPitches())
                                .map(p -> new Note(c.rhythm(), p))
                        ).collect(Collectors.toCollection(ArrayList<Note>::new));
        chordNotes.addAll(this.notes);
        return chordNotes.toArray(new Note[0]);
    }

}
