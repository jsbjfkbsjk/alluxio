package alluxio.client.file;

import alluxio.client.block.stream.BlockInStream;
import alluxio.client.block.stream.BlockOutStream;
import alluxio.client.block.stream.DataWriter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Native_TWO_TONE {
    // 加载 C++ 共享库
    static {
        System.load("C:/Users/25048/Downloads/shift_xor_2023/blockrecovery.dll");
    }

    public native void processStreams(InputStream[] inputs, OutputStream[] outputs);

    public List<BlockOutStream> create(List<BlockInStream> blockInStreams,List<BlockOutStream> blockOutStreams) {

        InputStream[] inputs = blockInStreams.stream()
                .map(in -> (InputStream) in)
                .toArray(InputStream[]::new);

        OutputStream[] outputs = blockOutStreams.stream()
                .map(out -> (OutputStream) out)
                .toArray(OutputStream[]::new);
        processStreams(inputs,outputs);
        return blockOutStreams;
    }
}
