import java.util.Arrays;

public class Main {
    private static final int PAGE_NUMBER = 4;
    private static final int ACCESS_SEQUENCE_SIZE = 12;
    private static final int BUFFER_SIZE_SMALL = 3;
    private static final int BUFFER_SIZE_LARGE = 4;
    private static final int RUNS_NUMBER = 1;
    private static final int POPULAR_VALUES_NUMBER = 2;
    private static final double POPULAR_VALUES_RATE = 0.4;
    private static final boolean USE_FIXED_SEQUENCE = true;
    private static final int[] FIXED_SEQUENCE = new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};

    public static void main(String[] args) {
        int[] bufferSmall = new int[BUFFER_SIZE_SMALL];
        int[] bufferLarge = new int[BUFFER_SIZE_LARGE];
        int beladyAnomalies = 0;
        for (int i = 0; i < RUNS_NUMBER; i++) {
            show("Run : " + (i + 1));
            int[] pageSequence = makeAccessSequence();
            initBuffer(bufferSmall);
            initBuffer(bufferLarge);
            int pageFaultsSmall = emulate(bufferSmall, pageSequence);
            show("Page faults for small buffer: " + pageFaultsSmall);
            int pageFaultsLarge = emulate(bufferLarge, pageSequence);
            show("Page faults for large buffer: " + pageFaultsLarge);
            if (pageFaultsSmall < pageFaultsLarge) {
                show("Belady anomaly seen!");
                beladyAnomalies++;
            }
        }
        show(String.format("Belady anomalies seen in %d from %d runs", beladyAnomalies, RUNS_NUMBER));

    }

    private static int emulate(int[] buffer, int[] pageSequence) {
        int pageFaults = 0;
        for (int i = 0; i < ACCESS_SEQUENCE_SIZE; i++) {
            show("Step " + (i + 1));
            show("Looking for page " + pageSequence[i]);
            if (isPageFault(buffer, pageSequence[i])) {
                show("Page fault!");
                pageFaults++;
                putPageToBuffer(buffer, pageSequence[i]);
            } else {
                show("Page hit!");
            }
            show("Buffer:" + Arrays.toString(buffer));
        }
        return pageFaults;
    }

    private static boolean isPageFault(int[] buffer, int pageNumber) {
        for (int i  : buffer) {
            if (i == pageNumber) {
                return false;
            }
        }
        return true;
    }

    private static void putPageToBuffer(int[] buffer, int pageNumber) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] < 0) {
                buffer[i] = pageNumber;
                return;
            }
        }
        for (int i = 1; i < buffer.length; i++) {
            buffer[i - 1] = buffer[i];
        }
        buffer[buffer.length - 1] = pageNumber;
    }

    private static void initBuffer(int[] buffer) {
        Arrays.fill(buffer, -1);
    }

    private static int[] makeAccessSequence(){
        int[] pageSequence = new int[ACCESS_SEQUENCE_SIZE];
        if (USE_FIXED_SEQUENCE) {
            pageSequence = FIXED_SEQUENCE;
        } else {
            for (int i = 0; i < ACCESS_SEQUENCE_SIZE; i++) {
                pageSequence[i] = (int) (Math.random() * PAGE_NUMBER);
            }
            if (POPULAR_VALUES_NUMBER > 0) {
                int[] popularValues = new int[POPULAR_VALUES_NUMBER];
                System.arraycopy(pageSequence, 0, popularValues, 0, POPULAR_VALUES_NUMBER);
                for (int j = 0; j < pageSequence.length; j++) {
                    int curVal = pageSequence[j];
                    if (Arrays.stream(popularValues).anyMatch(x -> x == curVal)) {
                        if (Math.random() < POPULAR_VALUES_RATE) {
                            pageSequence[j] = popularValues[(int) (Math.random() * POPULAR_VALUES_NUMBER)];
                        }
                    }
                }
            }
        }
        show("Page sequence: " + Arrays.toString(pageSequence));
        return pageSequence;
    }

    private static void show(Object obj) {
        System.out.println(obj);
    }
}