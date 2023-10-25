package zfkd;
import java.io.File;

import java.util.Comparator;

public class FileSizeComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            // Compare files based on their length (size)
            return Long.compare(file1.length(), file2.length());
        }
}