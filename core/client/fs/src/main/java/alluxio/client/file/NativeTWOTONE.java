package alluxio.client.file;

import java.nio.ByteBuffer;

public class NativeTWOTONE {
    // 加载 C++ 共享库
    static {
        System.load("/root/libshift_xor.so");
    }

    public native void processStreams(byte[] input, byte[] output);

    public native void recoverBuffer(byte[] buffer,byte[]two_tone_block);

    public byte[] create(byte[] b) {
        byte[]output = new byte[3*1184];
        processStreams(b,output);
        return output;
    }

    public void recover(ByteBuffer buf,byte[]two_tone_block) {
        int pos = buf.position();
        int lim = buf.limit();
        int len = buf.limit();
        if (len <= 0) return;

        byte[] tmp;

        if (buf.hasArray()) {
            // 堆内缓冲：可以直接拿到底层数组 + 偏移
            byte[] arr = buf.array();
            int base = buf.arrayOffset() + pos;
            recoverBuffer(arr,two_tone_block);  // 直接原地修改
        } else {
            // 直接缓冲或无数组：先拷到临时数组，JNI 改完再写回
            tmp = new byte[len];

            ByteBuffer dup = buf.duplicate();
            dup.position(0).limit(lim);
            dup.get(tmp);               // 读出

            // 调 JNI 修改 tmp
            recoverBuffer(tmp,two_tone_block);
            // 写回原 buffer 的相同区间
            dup.clear().position(0).limit(lim);
            dup.put(tmp);
        }
    }
}
