package com.ed522.secant.midi;

import org.jetbrains.annotations.NotNullByDefault;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

@NotNullByDefault
public class MidiPlayer {
    private final MidiChannel channel;
    private final MidiDevice device;
    private final Transmitter transmitter;
    private final Receiver receiver;

    public MidiPlayer(MidiDevice device, MidiChannel channel) throws MidiUnavailableException {
        this.channel = channel;
        this.device = device;
        this.transmitter = device.getTransmitter();
        this.receiver = device.getReceiver();
    }

}
