package net.runelite.client.audio;

import jaco.mp3.resources.BitstreamException;
import jaco.mp3.resources.Frame;
import jaco.mp3.resources.SoundStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public class MP3FileReader extends AudioFileReader
{
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException
    {
        return null;
    }

    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException
    {
        return null;
    }

    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException
    {
        return null;
    }

    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException
    {
        stream.mark(stream.available());
        SoundStream soundStream = new SoundStream(stream);

        Frame frame;
        try
        {
            frame = soundStream.readFrame();
        }
        catch (BitstreamException e)
        {
            throw new IOException(e);
        }

        if (frame == null)
        {
            throw new UnsupportedAudioFileException("could not read an MP3 chunk");
        }

        stream.reset();
        int channels = frame.mode() == Frame.SINGLE_CHANNEL ? 1 : 2;
        AudioFormat format = new AudioFormat(frame.frequency(), 16, channels, true, false);

        return new AudioInputStream(new MP3InputStream(stream), format, AudioSystem.NOT_SPECIFIED);
    }

    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException
    {
        return null;
    }

    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException
    {
        return getAudioInputStream(new FileInputStream(file));
    }
}
