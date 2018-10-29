package com.tomer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Tomer Cohen
 */
public class SolutionMulti {
    // We'll keep sequence numbers in the key order to ensure that the nodes inside the SortedSet are unique.
    // which effectively allows us to insert the nodes into a sorted set, keep it sorted without having to
    // sort later on a larger unsorted collection sine the nodes can come out of turn.
    private static final AtomicLong SEQUENCE_NUMBER_GENERATOR = new AtomicLong(0L);
    // a map that will keep all the latency nodes per minute, since we'd like to calculate the percentile
    // on a minute by minute basis. Keep the map concurrent as there may be multiple threads trying to read/write and
    // it is a global map for the solution.
    private final ConcurrentMap<LocalDateTime, SortedSet<EpochLatencyNode>> epochLatencyNodesPerMinute;
    // keep a simple set of processed times (by the minute), don't care about sorted here, and if the hashcode
    // is efficient enough the fetchtime is O(1) since a HashSet is backed by a HashMap so it should be fast.
    private final Set<LocalDateTime> processedDateTimes;
    // executor service to move the burden of the main thread to another one.
    private final ExecutorService executor;

    /**
     * Solution constructor
     */
    public SolutionMulti() {
        this.epochLatencyNodesPerMinute = new ConcurrentHashMap<>();
        this.processedDateTimes = new HashSet<>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * A class which keeps the result of an insertion of a node into the SortedSet.
     */
    public static class InsertResult {
        private final boolean printResult;
        private final LocalDateTime adjustedLocalDateOfOfNode;
        private final LocalDateTime printResultForDate;

        public InsertResult() {
            this(false, LocalDateTime.now(), LocalDateTime.now());
        }

        public InsertResult(boolean printResult, LocalDateTime adjustedLocalDateOfOfNode, LocalDateTime printResultForDate) {
            this.printResult = printResult;
            this.adjustedLocalDateOfOfNode = adjustedLocalDateOfOfNode;
            this.printResultForDate = printResultForDate;
        }

        public boolean isPrintResult() {
            return printResult;
        }

        public LocalDateTime getPrintResultForDate() {
            return printResultForDate;
        }

        public LocalDateTime getAdjustedLocalDateOfOfNode() {
            return adjustedLocalDateOfOfNode;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("InsertResult{");
            sb.append("printResult=").append(printResult);
            sb.append(", printResultForDate=").append(printResultForDate);
            sb.append('}');
            return sb.toString();
        }
    }


    /**
     * Insert a node into a sorted set.
     *
     * @param node The node to insert
     * @return The insertion result and if to print out the percentile for the minute since there shouldn't be any more
     * nodes coming in for that minute.
     */
    public Future<InsertResult> insertNode(EpochLatencyNode node) {
        return executor.submit(() -> {
            if (node.getLatency() < 0.0) {
                // can't be less than 0.
                throw new IllegalArgumentException("Latency cannot be less than 0.");
            }
            if (node.getLatency() > 150) {
                // discard latency over 150
                return new InsertResult();
            }
            LocalDateTime localDateTimeOfNode = node.getLocalDateTime();
            LocalDateTime adjustedLocalDateOfOfNode = localDateTimeOfNode.withSecond(0);
            epochLatencyNodesPerMinute.putIfAbsent(adjustedLocalDateOfOfNode, new TreeSet<>());
            SortedSet<EpochLatencyNode> value = epochLatencyNodesPerMinute.get(adjustedLocalDateOfOfNode);
            if (!value.isEmpty() && checkIfOutOfRange(node, value.last())) {
                // if out of range, discard
                return new InsertResult();
            }

            value.add(node);
            LocalDateTime previousRecordedTime = adjustedLocalDateOfOfNode.minusMinutes(1);
            InsertResult insertResult = new InsertResult(epochLatencyNodesPerMinute.containsKey(previousRecordedTime) && !processedDateTimes.contains(previousRecordedTime), adjustedLocalDateOfOfNode, previousRecordedTime);
            if (insertResult.printResult) {
                calculateAndPrintPercentile(previousRecordedTime, 90);
            }
            return insertResult;
        });
    }

    private boolean checkIfOutOfRange(EpochLatencyNode node, EpochLatencyNode lastRecorededNode) {
        return Duration.between(lastRecorededNode.getLocalDateTime(), node.getLocalDateTime()).getSeconds() > 60;
    }

    /**
     * Calculate the percentile for a minute, specified by the localDateTime and the percentile parameter
     *
     * @param localDateTime The minute for which to calculate the percentile for.
     * @param percentile    The percentile to calculate for.
     */
    public void calculateAndPrintPercentile(LocalDateTime localDateTime, int percentile) {
        SortedSet<EpochLatencyNode> epochLatencyNodes = epochLatencyNodesPerMinute.get(localDateTime);
        double i = (percentile / 100.0) * epochLatencyNodes.size();
        EpochLatencyNode[] epochLatencyArray = epochLatencyNodes.toArray(new EpochLatencyNode[0]);
        EpochLatencyNode epochLatencyNode = epochLatencyArray[(int) i];
        System.out.println(epochLatencyNode);
        processedDateTimes.add(localDateTime);
    }

    /**
     * This class represents a node inside the set, it's comparable so it can be sorted based upon the epochtime/sequence
     * number
     */
    public static class EpochLatencyNode implements Comparable<EpochLatencyNode> {
        private final long sequenceNumber;
        private final long epochTime;
        private final double latency;
        private LocalDateTime localDateTime;

        public EpochLatencyNode(long sequenceNumber, long epochTime, double latency) {
            this.sequenceNumber = sequenceNumber;
            this.epochTime = epochTime;
            this.latency = latency;
            this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.of("UTC"));
        }

        public long getEpochTime() {
            return epochTime;
        }

        public double getLatency() {
            return latency;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public long getSequenceNumber() {
            return sequenceNumber;
        }

        public static EpochLatencyNode from(long epochTime, double latency) {
            return new EpochLatencyNode(SEQUENCE_NUMBER_GENERATOR.getAndIncrement(), epochTime, latency);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EpochLatencyNode)) return false;

            EpochLatencyNode that = (EpochLatencyNode) o;

            if (sequenceNumber != that.sequenceNumber) return false;
            if (epochTime != that.epochTime) return false;
            return Double.compare(that.latency, latency) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = (int) (sequenceNumber ^ (sequenceNumber >>> 32));
            result = 31 * result + (int) (epochTime ^ (epochTime >>> 32));
            temp = Double.doubleToLongBits(latency);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            String utc = ZonedDateTime.of(localDateTime.toLocalDate(), localDateTime.toLocalTime(), ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
            builder.append(utc).append(" ").append(latency);
            return builder.toString();
        }

        @Override
        public int compareTo(EpochLatencyNode o) {
            int epochCompare = Long.compare(this.epochTime, o.getEpochTime());
            if (epochCompare == 0) {
                return Long.compare(this.sequenceNumber, o.getSequenceNumber());
            }
            return epochCompare;
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SolutionMulti solution = new SolutionMulti();
        Scanner scanner = new Scanner(System.in);
        Future<SolutionMulti.InsertResult> insertResult = null;
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            String[] split = nextLine.split(" ");
            insertResult = solution.insertNode(EpochLatencyNode.from(Long.parseLong(split[0]), Double.parseDouble(split[1])));

        }
        solution.calculateAndPrintPercentile(insertResult.get().getAdjustedLocalDateOfOfNode(), 90);
    }
}
