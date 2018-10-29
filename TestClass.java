package com.tomer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Tomer Cohen
 */
public class TestClass {
    @Test
    public void testName() throws Exception {
        Solution solution = new Solution();
        LineIterator lineIterator = IOUtils.lineIterator(TestClass.class.getResourceAsStream("/input001.txt"), "UTF-8");
        Solution.InsertResult insertResult = null;
        while (lineIterator.hasNext()) {
            String next = lineIterator.next();
            String[] split = StringUtils.split(next, ' ');
            Solution.EpochLatencyNode from = Solution.EpochLatencyNode.from(Long.parseLong(split[0]), Double.parseDouble(split[1]));
            insertResult = solution.insertNode(from);
            if (insertResult.isPrintResult()) {
                System.out.println(solution.calculatePercentile(insertResult.getPrintResultForDate(), 90));
            }
        }
//
        System.out.println(solution.calculatePercentile(insertResult.getAdjustedLocalDateOfOfNode(), 90));
//        long initial = 962668800L;
//        for (int j = 0; j < 10000; j++) {
//            Solution.InsertResult insertResult = solution.insertNode(new Solution.EpochLatencyNode(initial, 100.0));
//            if (insertResult.isPrintResult()) {
//                System.out.println(solution.calculatePercentile(insertResult.getPrintResultForDate(), 90));
//            }
//            initial++;
//
//        }

    }
}
