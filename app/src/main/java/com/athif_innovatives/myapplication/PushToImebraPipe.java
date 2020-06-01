package com.athif_innovatives.myapplication;

import com.imebra.MutableMemory;
import com.imebra.PipeStream;
import com.imebra.StreamWriter;

import java.io.IOException;
import java.io.InputStream;

public class PushToImebraPipe implements Runnable {

    private PipeStream mImebraPipe;    // The Pipe into which we push the data
    private InputStream mStream; // The InputStream from which we read the data

    public PushToImebraPipe(com.imebra.PipeStream pipe, InputStream stream) {
        mImebraPipe = pipe;
        mStream = stream;
    }

    @Override
    public void run() {
        StreamWriter pipeWriter = new StreamWriter(mImebraPipe.getStreamOutput());
        try {

            // Buffer used to read from the stream
            byte[] buffer = new byte[128000];
            MutableMemory memory = new MutableMemory();

            // Read until we reach the end
            for (int readBytes = mStream.read(buffer); readBytes >= 0; readBytes = mStream.read(buffer)) {

                // Push the data to the Pipe
                if(readBytes > 0) {
                    memory.assign(buffer);
                    memory.resize(readBytes);
                    pipeWriter.write(memory);
                }
            }
        }
        catch(IOException e) {
        }
        finally {
            pipeWriter.delete();
            mImebraPipe.close(50000);
        }
    }
}
