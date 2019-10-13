package evan.com.algo;

/**
 * Created by shidu on 19/4/8.
 * https://github.com/wangzheng0822/algo/blob/master/java/11_sorts/Sorts.java
 */

public class Sorts {

    // 插入排序，a表示数组，n表示数组大小
    public static void insertionSort(int[] a, int n) {
        if (n <= 1) return;  //4,5,6,1,3,2

        for (int i = 1; i < n; ++i) {
            int value = a[i];
            int j = i - 1;
            // 查找要插入的位置并移动数据
            for (; j >= 0; --j) {
                if (a[j] > value) {
                    a[j+1] = a[j];
                } else {
                    break;
                }
            }
            System.out.println("j = "+j);
            a[j+1] = value;
        }
    }

    // 选择排序，a表示数组，n表示数组大小
    public static void selectionSort(int[] a, int n) {
        if (n <= 1) return;

        for (int i = 0; i < n - 1; ++i) {
            // 查找最小值
            int minIndex = i;
            for (int j = i + 1; j < n; ++j) {
                if (a[j] < a[minIndex]) {
                    minIndex = j;
                }
            }

            // 交换
            int tmp = a[i];
            a[i] = a[minIndex];
            a[minIndex] = tmp;
        }
    }

    public static void main(String[] args){
//        int[] a = {4,5,6,1,3,2};
        int[] a = {4,5,6,3,1,2};
//        Sorts.insertionSort(a,a.length);
        Sorts.selectionSort(a,a.length);
        System.out.println("result");
        for (int t: a){
            System.out.println(t);
        }
    }
}
