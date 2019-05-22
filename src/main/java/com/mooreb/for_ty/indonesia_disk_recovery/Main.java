package com.mooreb.for_ty.indonesia_disk_recovery;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private final static int bufferSize = 64*1024*1024;
    private final static int maxImageSize = 8*1024*1024;
    private final static byte ff = (byte)0xff;
    private final static byte d8 = (byte)0xd8;
    private final static byte d9 = (byte)0xd9;
    private final static byte e1 = (byte)0xe1;

    private static class Coordinate {
        private final long offset;
        private final long length;

        public Coordinate(long offset, long length) {
            this.offset = offset;
            this.length = length;
        }

        public long getOffset() {
            return offset;
        }

        public long getLength() {
            return length;
        }
    }

    public static void main(String[] argv) throws IOException {
        int imageNumber = 0;
        final List<Coordinate> coordinates = getCoordinates();
        final RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
        for(final Coordinate coordinate : coordinates) {
            randomAccessFile.seek(coordinate.getOffset());
            final byte[] buffer = new byte[(int)coordinate.getLength()];
            randomAccessFile.read(buffer);
            writeFile(buffer, ++imageNumber);
        }
    }

    private static void writeFile(final byte[] buffer, int imageNumber) throws IOException {
        final File tld = new File("/Users/mooreb/Desktop/ty recovery/");
        final String fname = String.format("img-%04d.jpg", imageNumber);
        final File file = new File(tld, fname);
        final FileOutputStream fos = new FileOutputStream(file);
        // fos.write(truncateBuffer(buffer));
        fos.write(buffer);
        fos.flush();
        fos.close();
    }

    private static byte[] truncateBuffer(final byte[] buffer) {
        int actualLength = -1;
        for(int i=buffer.length-2; i>=0; i--) {
            if((ff == buffer[i]) && (d9 == buffer[i+1])) {
                actualLength = i+2;
                break;
            }
        }
        byte[] retval = new byte[actualLength];
        System.arraycopy(buffer, 0, retval, 0, actualLength);
        return retval;
    }

    private static List<Coordinate> getCoordinates() throws IOException {
        List<Coordinate> retval = new ArrayList<>();
        List<Long> offsets = getOffsets();
        for(int i=1; i< offsets.size(); i++) {
            long prevOffset = offsets.get(i-1);
            long thisOffset = offsets.get(i);
            long distance = thisOffset - prevOffset;
            long length = Math.min(maxImageSize, distance);
            retval.add(new Coordinate(prevOffset, length));
        }
        retval.add(new Coordinate(offsets.get(offsets.size()-1), maxImageSize));
        return retval;
    }

    private static List<Long> getOffsets() throws IOException {
        List<Long> retval = new ArrayList<>();
        int potentialImagesFound = 0;
        double targetPercent = -1;
        final long totalBytes = getFile().length();
        long totalBytesRead = 0L;
        final InputStream inputStream = getInputStream();
        byte[] buffer = newBuffer();
        int bytesRead = inputStream.read(buffer);
        while(bytesRead > 0) {
            if(totalBytesRead > 4685318144L) break;
            double percentDone = (0.0+totalBytesRead)/totalBytes;
            if(percentDone > targetPercent) {
                targetPercent += 0.02;
                System.out.println("" + System.currentTimeMillis() + " percentDone: " + percentDone);
            }
            for (int i = 0; i < (bytesRead - 10); i++) {
                if (potentialImageDataAtOffset(buffer, i)) {
                    final long offset = totalBytesRead + i;
                    System.out.println("potential image #" + ++potentialImagesFound + " at " + offset);
                    retval.add(offset);
                }
            }
            System.arraycopy(buffer, bytesRead-10, buffer, 0, 10);
            if(0 == totalBytesRead) {
                totalBytesRead -= 10;
            }
            totalBytesRead += bytesRead;
            bytesRead = inputStream.read(buffer, 10, buffer.length-10);
        }
        return retval;
    }

    private static byte[] newBuffer() {
        return new byte[bufferSize];
    }

    private static InputStream getInputStream() throws IOException  {
        return new FileInputStream(getFile());
    }

    private static File getFile() {
        return new File("/Users/mooreb/Desktop/ty-sd-card-recovery-project");
    }


    private static boolean potentialImageDataAtOffset(byte[] buffer, int offset) {
        final byte first   = buffer[offset];      // 0xFF
        final byte second  = buffer[offset + 1];  // 0xD8
        final byte third   = buffer[offset + 2];  // 0xFF
        final byte fourth  = buffer[offset + 3];  // 0xE1
        final byte len1    = buffer[offset + 4];
        final byte len2    = buffer[offset + 5];
        final byte seventh = buffer[offset + 6]; // E
        final byte eighth  = buffer[offset + 7]; // x
        final byte ninth   = buffer[offset + 8]; // i
        final byte tenth   = buffer[offset + 9]; // f
        return ((ff == first) &&
                (d8 == second) &&
                (ff == third) &&
                (e1 == fourth) &&
                ('E' == seventh) &&
                ('x' == eighth) &&
                ('i' == ninth) &&
                ('f' == tenth)
        );
    }
}
