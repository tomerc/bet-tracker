package com.tomer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Tomer Cohen
 */
public class Solution {
    private final ConcurrentMap<LocalDateTime, List<EpochLatencyNode>> epochLatencyNodesPerMinute;
    private final Set<LocalDateTime> processDateTimes;

    public Solution() {
        this.epochLatencyNodesPerMinute = new ConcurrentHashMap<>();
        this.processDateTimes = new HashSet<>();
    }

    public static class InsertResult {
        private final boolean printResult;
        private final LocalDateTime adjustedLocalDateOfOfNode;
        private final LocalDateTime printResultForDate;

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


    public InsertResult insertNode(EpochLatencyNode node) {
        LocalDateTime localDateTimeOfNode = node.getLocalDateTime();
        LocalDateTime adjustedLocalDateOfOfNode = localDateTimeOfNode.withSecond(0);
        if (epochLatencyNodesPerMinute.containsKey(adjustedLocalDateOfOfNode)) {
            List<EpochLatencyNode> epochLatencyNodes = epochLatencyNodesPerMinute.get(adjustedLocalDateOfOfNode);
            epochLatencyNodes.add(node);
        } else {
            List<EpochLatencyNode> value = new ArrayList<>();
            epochLatencyNodesPerMinute.put(adjustedLocalDateOfOfNode, value);
            value.add(node);
        }

        LocalDateTime previousRecordedTime = adjustedLocalDateOfOfNode.minusMinutes(1);
        return new InsertResult(epochLatencyNodesPerMinute.containsKey(previousRecordedTime) && !processDateTimes.contains(adjustedLocalDateOfOfNode), adjustedLocalDateOfOfNode, previousRecordedTime);
    }

    public EpochLatencyNode calculatePercentile(LocalDateTime localDateTime, int percentile) {
        List<EpochLatencyNode> epochLatencyNodes = epochLatencyNodesPerMinute.get(localDateTime);
        Collections.sort(epochLatencyNodes);
        double i = (percentile / 100.0) * epochLatencyNodes.size();
        double latency = epochLatencyNodes.get((int) i).getLatency();
        long epochSecond = Instant.ofEpochSecond(localDateTime.toEpochSecond(ZoneOffset.UTC)).getEpochSecond();
        return new EpochLatencyNode(epochSecond, latency);
    }


    public static class EpochLatencyNode implements Comparable<EpochLatencyNode> {
        private final long epochTime;
        private final double latency;
        private LocalDateTime localDateTime;

        public EpochLatencyNode(long epochTime, double latency) {
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

        public static EpochLatencyNode from(long epochTime, double latency) {
            return new EpochLatencyNode(epochTime, latency);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EpochLatencyNode that = (EpochLatencyNode) o;

            if (epochTime != that.epochTime) return false;
            return Double.compare(that.latency, latency) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = (int) (epochTime ^ (epochTime >>> 32));
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
            return Long.compare(this.epochTime, o.getEpochTime());
        }
    }


    public static void main(String[] args) {
        Solution solution = new Solution();
        Scanner scanner = new Scanner(System.in);
        Solution.InsertResult insertResult = null;
        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            String[] split = nextLine.split(" ");
            insertResult = solution.insertNode(EpochLatencyNode.from(Long.parseLong(split[0]), Double.parseDouble(split[1])));
            if (insertResult.isPrintResult()) {
                System.out.println(solution.calculatePercentile(insertResult.getPrintResultForDate(), 90));
            }
        }
        System.out.println(solution.calculatePercentile(insertResult.getAdjustedLocalDateOfOfNode(), 90));
    }
}
