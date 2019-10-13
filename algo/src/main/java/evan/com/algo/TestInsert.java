package evan.com.algo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by shidu on 19/4/5.
 */

public class TestInsert {

    private int[] cnts = new int[256];
    private Queue<Character> queue = new LinkedList<>();

    public void Insert(char ch) {
        cnts[ch]++;
        queue.add(ch);
        while (!queue.isEmpty() && cnts[queue.peek()] > 1)
            queue.poll();
    }

    public char FirstAppearingOnce() {
        return queue.isEmpty() ? '#' : queue.peek();
    }
//    6, -3, -2, 7, -15, 1, 2, 2
    public int FindGreatestSumOfSubArray(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int greatestSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int val : nums) {
            sum = sum <= 0 ? val : sum + val;
            greatestSum = Math.max(greatestSum, sum);
        }
        return greatestSum;
    }

    public int longestSubStringWithoutDuplication(String str) {
        int curLen = 0;
        int maxLen = 0;
        int[] preIndexs = new int[26];
        Arrays.fill(preIndexs, -1);
        for (int curI = 0; curI < str.length(); curI++) {
            int c = str.charAt(curI) - 'a';
            int preI = preIndexs[c];
            if (preI == -1 || curI - preI > curLen) {
                curLen++;
            } else {
                maxLen = Math.max(maxLen, curLen);
                curLen = curI - preI;
            }
            preIndexs[c] = curI;
        }
        maxLen = Math.max(maxLen, curLen);
        return maxLen;
    }


    public static void main(String[] args){
        TestInsert testInsert = new TestInsert();
        testInsert.Insert('g');
        testInsert.Insert('o');
        testInsert.Insert('o');
//        testInsert.Insert('g');
//        testInsert.Insert('l');
//        testInsert.Insert('e');
        char result = testInsert.FirstAppearingOnce();
        System.out.println("result = "+result);

        String s = "nihaoHHH换行";

        System.out.println("charAt(8) = "+ s.charAt(8));

        int[] nums =  {6, -3, -2, 7, -15, 1, 2, 2};
        int greatest = testInsert.FindGreatestSumOfSubArray(nums);
        System.out.println("FindGreatestSumOfSubArray = "+greatest);


        int lo = testInsert.longestSubStringWithoutDuplication("arabcfacfr");
        System.out.println("lo = "+lo);

    }

}
