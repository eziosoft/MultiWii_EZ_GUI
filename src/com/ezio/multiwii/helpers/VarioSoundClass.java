package com.ezio.multiwii.helpers;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class VarioSoundClass {

	boolean m_stop = false;
	AudioTrack m_audioTrack;
	Thread m_playSoundThread;

	private final int sampleRate = 8000;
	private final int numSamples = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
	private final double sample[] = new double[numSamples];
	private double freqOfTone = 1000; // hz

	private final byte generatedSnd[] = new byte[2 * numSamples];

	Runnable m_noiseGenerator = new Runnable() {
		public void run() {
			// while (!m_stop) {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			for (int i = 0; i < numSamples; ++i) {
				sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
			}

			// convert to 16 bit pcm sound array
			// assumes the sample buffer is normalised.
			int idx = 0;
			for (final double dVal : sample) {
				// scale to maximum amplitude
				final short val = (short) ((dVal * 32767));
				// in 16 bit wav PCM, first byte is the low order byte
				generatedSnd[idx++] = (byte) (val & 0x00ff);
				generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

			}

			m_audioTrack.write(generatedSnd, 0, generatedSnd.length);
			m_audioTrack.play();

			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// }
		}
	};

	public void Play(double frequency) {
		freqOfTone = frequency;

		m_stop = false;
		m_audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length, AudioTrack.MODE_STREAM);

		// m_audioTrack.play();

		m_playSoundThread = new Thread(m_noiseGenerator);
		m_playSoundThread.start();
	}

	void stop() {
		m_stop = true;
		m_audioTrack.stop();
	}
}
