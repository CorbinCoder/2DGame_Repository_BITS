package turd.game.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;

import org.lwjgl.stb.STBVorbisInfo;
import static org.lwjgl.stb.STBVorbis.*;

import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.*;

//Creates buffers to handle .wav I/O.
public class AudioBuffer {

	private final int iBufferID;
	
	private ByteBuffer bbVorbis = null;
	private ShortBuffer pcm = null;

	// Initializes a buffer. Encodes data using STBVorbis.
	public AudioBuffer(String file) throws Exception {
		this.iBufferID = alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			pcm = readVorbis(file, 32 * 1024, info);

			AL10.alBufferData(this.iBufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm,
					info.sample_rate());
		}
	}

	// Returns buffer ID.
	public int getBufferID() {
		return this.iBufferID;
	}

	// Deletes buffers to prevent open I/O.
	public void cleanUp() {
		AL10.alDeleteBuffers(this.iBufferID);
		if (pcm != null) {
			MemoryUtil.memFree(pcm);
		}
	}

	// Reads .wav files and decodes using STBVorbis.
	private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
		try {
			bbVorbis = AudioUtils.ioResourceToByteBuffer(resource, bufferSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = stb_vorbis_open_memory(bbVorbis, error, null);
		if (decoder == NULL) {
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
		}
		
		stb_vorbis_get_info(decoder, info);
		
		int channels = info.channels();
		
		ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);
		 
		stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
		
		stb_vorbis_close(decoder);
		
		return pcm;
	}
}