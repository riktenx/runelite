package net.runelite.client.audio;

import jaco.mp3.resources.BitstreamException;
import jaco.mp3.resources.Decoder;
import jaco.mp3.resources.DecoderException;
import jaco.mp3.resources.Frame;
import jaco.mp3.resources.SampleBuffer;
import jaco.mp3.resources.SoundStream;

import java.io.IOException;
import java.io.InputStream;

class MP3InputStream extends InputStream
{
    private final SoundStream soundStream;
    private final Decoder decoder;

    private byte[] currentSamples = null;
    private int currentOffset = 0;

    public MP3InputStream(InputStream stream)
    {
        soundStream = new SoundStream(stream);
        decoder = new Decoder();
    }

    @Override
    public int read() throws IOException {
        if (currentSamples == null)
        {
            if (!pullNextSamples())
            {
                return -1;
            }
        }

        byte next = currentSamples[currentOffset++];
        if (currentOffset == currentSamples.length)
        {
            currentSamples = null;
            currentOffset = 0;
        }

        return next & 0xff;
    }

    private boolean pullNextSamples() throws IOException
    {
        try
        {
            Frame frame = soundStream.readFrame();
            if (frame == null)
            {
                return false;
            }

            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(frame, soundStream);
            currentSamples = toBytes(samples.getBuffer());
            soundStream.closeFrame();
        }
        catch (BitstreamException | DecoderException e)
        {
            throw new IOException(e);
        }

        return true;
    }

    private static byte[] toBytes(short[] samples)
    {
        byte[] buf = new byte[samples.length * 2];
        int i = 0;

        for (short sample : samples)
        {
            buf[i++] = (byte) sample;
            buf[i++] = (byte) (sample >>> 8);
        }

        return buf;
    }
}
