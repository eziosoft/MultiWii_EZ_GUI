/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.helpers;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TTS implements TextToSpeech.OnInitListener {
	private TextToSpeech tts;
	public static final int TTS_CHECK_CODE = 2345;
	Context context;
	public boolean TTSinit = false;
	boolean inicjalizacja = false;
	String tag = "smsreader";
	String text;

	private void CreateTTS() {
		Log.d(tag, "CreateTTS");

		if (!inicjalizacja) {

			tts = new TextToSpeech(context, this);

			inicjalizacja = true;

		}
	}

	public TTS(Context context) {
		this.context = context;
		Log.d(tag, "text to speach init TTSinit " + String.valueOf(TTSinit));
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.ENGLISH);
			Log.d("aaa", Locale.getDefault().getLanguage());
			if (Locale.getDefault().getLanguage().equals("de")) {
				result = tts.setLanguage(Locale.getDefault());
				Log.d("aaa", "german");
			}

			if (Locale.getDefault().getLanguage().equals("hu")) {
				result = tts.setLanguage(Locale.getDefault());
				Log.d("aaa", "hungarian");
			}

			if (Locale.getDefault().getLanguage().equals("pl")) {
				result = tts.setLanguage(Locale.getDefault());
				Log.d("aaa", "polish");
			}

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			}

			TTSinit = true;
			tts.speak(this.text, TextToSpeech.QUEUE_ADD, null);

		} else {
			TTSinit = false;
		}

		Log.d(tag, "text to speach init status " + String.valueOf(status));
		Log.d(tag, "text to speach init TTSinit " + String.valueOf(TTSinit));
	}

	public void Speak(String text) {

		Log.d(tag, "Speak:" + text);
		if (TTSinit) {

			tts.speak(text, TextToSpeech.QUEUE_ADD, null);
		} else {
			this.text = text;
			CreateTTS();
		}

	}

}
