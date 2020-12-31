package com.evan.primary.regular;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 权威来源
 * 1，https://docs.oracle.com/javase/tutorial/essential/regex/index.html
 * 2，正则表达式30分钟入门教程 https://deerchao.cn/tutorials/regex/regex.htm
 * 3，项目中使用的demo
 */
public class RegularTest {

    public static void main(String[] args) {

        // 正则表达式30分钟: 贪婪与懒惰
        String target = "aabab";
//        String regex = "a.*b";
        // I found the text "aabab" starting at index 0 and ending at index 5.

        String regex = "a.*?b";
        // I found the text "aab" starting at index 0 and ending at index 3.
        // I found the text "ab" starting at index 3 and ending at index 5.

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);

        //  while (matcher.find())
        //  if (matcher.find())
        //  while if 不同，当有多个匹配结果时while会全打印，if只打印第一个结果。

        boolean found = false;
        while (matcher.find()) {
//        if (matcher.find()) {
            String result =
                    String.format("I found the text" +
                                    " \"%s\" starting at " +
                                    "index %d and ending at index %d.%n",
                            matcher.group(),
                            matcher.start(),
                            matcher.end());
            found = true;
            System.out.println(result);
        }
        if (!found) {
//            String.format("No match found.%n");
            System.out.println("No match found");
        }

    }
}
