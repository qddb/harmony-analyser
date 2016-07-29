package harmanal;

import java.io.File;
import java.util.Vector;
import javax.sound.midi.*;

/**
 * Class to handle all the MIDI related events
 */

public class MidiHandler {
	
	public Sequencer sequencer;
	public Synthesizer synthesizer;
	public MidiDevice inputDevice;
	public MidiDevice outputDevice;
	public MidiDecoder decoder;
	
	public MidiChannel[] channels; 
	public Instrument[] instruments;
	private int instrument = 0;
	private int channel = 0;
	private int volume = 100;
	
	public static final int LONG = 0;
	public static final int SHORT = 1;
	
	private int length = 0;
	
	public static final int TOGETHER = 0;
	public static final int SEPARATE = 1;
	
	private int playMode = 0;
	
	public int getInstrument() {
		return instrument;
	}
	
	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getPlayMode() {
		return playMode;
	}
	
	public void setPlayMode(int playMode) {
		this.playMode = playMode;
	}
		
	/**
	 * Get list of MIDI input devices
	 */
	
	public String[] getInputDeviceList() {
		
		MidiDevice device;
		MidiDevice.Info[] infoMidiDevices = MidiSystem.getMidiDeviceInfo();
		Vector<MidiDevice.Info> inputDeviceInfos = new Vector<MidiDevice.Info>();
		
		for (int i = 0; i < infoMidiDevices.length; i++) {
		    try {
		        device = MidiSystem.getMidiDevice(infoMidiDevices[i]);
		        if (device.getClass().getSimpleName().endsWith("InDevice")) {
			        inputDeviceInfos.add(infoMidiDevices[i]);
			    }
		    } catch (MidiUnavailableException e) {
		    	e.printStackTrace();
		    }
		}
		
		String[] result = new String[inputDeviceInfos.size()];
		
		for (int i = 0; i < inputDeviceInfos.size(); i++) {
			result[i] = inputDeviceInfos.get(i).getName();
		}
		
		return result;
	}
	
	/**
	 * Get a MIDI device based on its name 
	 */
	
	public MidiDevice getMidiDevice(String name) {
		MidiDevice.Info[] infoMidiDevices = MidiSystem.getMidiDeviceInfo();
		
		for (int i = 0; i < infoMidiDevices.length; i++) {
		    try {
		    	if (infoMidiDevices[i].getName().equals(name) && isInputDevice(MidiSystem.getMidiDevice(infoMidiDevices[i]))) {
		    		return MidiSystem.getMidiDevice(infoMidiDevices[i]);
		    	}	    	
		    } catch (MidiUnavailableException e) {
		    	
		    	e.printStackTrace();
		    }
		}
		return null;
	}
	
	/**
	 * Finds out if a MIDI device is Input device
	 */
	
	public boolean isInputDevice(MidiDevice device) {
		return device.getClass().getSimpleName().endsWith("InDevice");
	}
	
	/**
	 * Initializes the MidiHandler with the custom Sequencer, Synthesizer, Input and Output devices or null values for default
	 */
	
	void initialize(Sequencer sequencer, Synthesizer synthesizer, MidiDevice inputDevice, MidiDevice outputDevice, MidiDecoder decoder) {
		try {
			if (sequencer != null) {
				this.sequencer = sequencer;
			} else {
				this.sequencer = MidiSystem.getSequencer();
			}
			this.sequencer.open();
			
			if (synthesizer != null) {
				this.synthesizer = synthesizer;
			} else {
				this.synthesizer = MidiSystem.getSynthesizer();
			}
			this.synthesizer.open();
			channels = this.synthesizer.getChannels();
			instruments = this.synthesizer.getDefaultSoundbank().getInstruments();
			
			if (inputDevice != null) {
				this.inputDevice = inputDevice;
				this.inputDevice.open();
			}
			
			if (outputDevice != null) {
				this.outputDevice = outputDevice;
				this.outputDevice.open();
			}
			
			if (decoder != null) {
				this.decoder = decoder;
			} else {
				this.decoder = new MidiDecoder();
			}

		} catch (MidiUnavailableException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects the input to the synthesizer
	 */
	
	void connectInputSynthesizer() {
		try {
			inputDevice.getTransmitter().setReceiver(synthesizer.getReceiver());
		} catch (MidiUnavailableException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Connects the input to the decoder
	 */
	
	void connectInputDecoder() {
		try {
			inputDevice.getTransmitter().setReceiver(decoder);
		} catch (MidiUnavailableException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Closes all possible devices used in the application
	 */
	
	void close() {
		if (inputDevice != null) {
			if (inputDevice.isOpen()) {
				inputDevice.close();
			}
		}
		if (synthesizer != null) {
			if (synthesizer.isOpen()) {
				synthesizer.close();
			}
		}
		if (sequencer != null) {
			if (sequencer.isOpen()) {
				sequencer.close();
			}
		}
		if (outputDevice != null) {
			if (outputDevice.isOpen()) {
				outputDevice.close();
			}
		}
		if (decoder != null) {
			if (decoder.isOpen()) {
				decoder.close();
			}
		}		
	}
	
	public void play(Tone tone) {
		synthesizer.loadInstrument(instruments[instrument]);
		
		channels[channel].noteOn(tone.getNumber(),tone.getVolume());
		if (length == SHORT) {
			channels[channel].noteOff(tone.getNumber());
		}
	}
	
	public void play(Harmony harmony) {
		synthesizer.loadInstrument(instruments[instrument]);
		
		for (Tone tone : harmony.tones) {
			play(tone);
		}
	}
	
	public void playMidi(String inputFile) {
		try {
			File file = new File("resources/" + inputFile);
			Sequence sekvencia = MidiSystem.getSequence(file);
			sequencer.setSequence(sekvencia); 
			sequencer.start();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public Harmony getBufferHarmony() {
		String buffer = decoder.getBuffer();
		if (buffer.equals("")) {
			return null;
		}
		String[] stringArray = buffer.split(" ");
		int[] intArray = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			intArray[i] = Integer.parseInt(stringArray[i]);
		}
		
		return new Harmony(intArray);
	}
	
}

class MidiDecoder implements Receiver {
	String buffer = "";

	public void close() {
		buffer = "";
	}
	
	public boolean isOpen() {
		if (buffer.length() != 0) {
			return true;
		} else {
			return false;
		}
	}

	public String getBuffer() {
		return buffer;
	}
	
	public void send(MidiMessage message, long lTimeStamp) {
		if (message instanceof ShortMessage) {
			if ((((ShortMessage)message).getCommand() == 0x90) && (((ShortMessage)message).getData2() != 0)) {
				buffer += (((ShortMessage)message).getData1() + " ");
			}
		}
	}
}