package evan.com.algo;

/**
 * Created by shidu on 19/4/5.
 * https://github.com/wangzheng0822/algo/blob/master/java/07_linkedlist/LinkedListAlgo.java
 */
/**
 * 1) 单链表反转
 * 2) 链表中环的检测
 * 3) 两个有序的链表合并
 * 4) 删除链表倒数第n个结点
 * 5) 求链表的中间结点
 *
 * Author: Zheng
 */
public class LinkedListAlgo {

    // 单链表反转  不带头节点操作反转
    public static Node reverse(Node list) {
        Node curr = list, pre = null;
        while (curr != null) {
            Node next = curr.next;  //防止链表断裂，先存next节点，下面curr好节点更新
            curr.next = pre;
            pre = curr;
            curr = next;
        }
        return pre;

        // error :  curr.next = pre;  使得链表断裂
//        Node curr = list, pre = null;
//        while (curr != null) {
//            curr.next = pre;
//            pre = curr;
//            curr = curr.next;
//        }
//        return pre;

        // error : next.next = newList.next; 使得链表断裂
//        Node newList = new Node(-1,null);
//        while (list != null) {
//            Node next = list.next;
//            if (next == null) break;
//            next.next = newList.next;
//            newList.next = next;
//            list = next;
//        }
//        return newList.next;

        //带头节点来操作反转
//        Node newList = new Node(-1,null);
//        while (list != null) {
//            Node next = list.next;
//            list.next = newList.next; // 让当前头节点指向 新头节点的下一个节点
//            newList.next = list;
//            list = next;
//        }
//        return newList.next;
    }

    public static Node ReverseList(Node head) {
        if (head == null || head.next == null)
            return head;
        Node next = head.next;
        head.next = null;
        Node newHead = ReverseList(next);
        next.next = head;
        return newHead;
    }

    // 检测环
    public static boolean checkCircle(Node list) {
        if (list == null) return false;

        Node fast = list.next;
        Node slow = list;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;

            if (slow == fast) return true;
        }

        return false;
    }

    // 有序链表合并
    public static Node mergeSortedLists(Node la, Node lb) {
        if (la == null) return lb;
        if (lb == null) return la;

        Node p = la;
        Node q = lb;
        Node head;
        if (p.data < q.data) {
            head = p;
            p = p.next;
        } else {
            head = q;
            q = q.next;
        }
        Node r = head;

        while (p != null && q != null) {
            if (p.data < q.data) {
                r.next = p;
                p = p.next;
            } else {
                r.next = q;
                q = q.next;
            }
            r = r.next;
        }

        if (p != null) {
            r.next = p;
        } else {
            r.next = q;
        }

        return head;
    }

    public static Node Merge(Node list1, Node list2) {
        Node head = new Node(-1,null);
        Node cur = head;
        while (list1 != null && list2 != null) {
            if (list1.data <= list2.data) {
                cur.next = list1;
                list1 = list1.next;
            } else {
                cur.next = list2;
                list2 = list2.next;
            }
            cur = cur.next;
        }
        if (list1 != null)
            cur.next = list1;
        if (list2 != null)
            cur.next = list2;
        return head.next;
    }


    // 删除倒数第K个结点
    public static Node deleteLastKth(Node list, int k) {
        Node fast = list;
        int i = 1;
        while (fast != null && i < k) {
            fast = fast.next;
            ++i;
        }

        if (fast == null) return list;

        Node slow = list;
        Node prev = null;
        while (fast.next != null) {
            fast = fast.next;
            prev = slow;
            slow = slow.next;
        }

        if (prev == null) {
            list = list.next;
        } else {
            prev.next = prev.next.next;
        }
        return list;
    }

    // 求中间结点
    public static Node findMiddleNode(Node list) {
        if (list == null) return null;

        Node fast = list;
        Node slow = list;

        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }

        return slow;
    }

    public static void printAll(Node list) {
        Node p = list;
        while (p != null) {
            System.out.print(p.data + " ");
            p = p.next;
        }
        System.out.println();
    }

    public static Node createNode(int value) {
        return new Node(value, null);
    }

    public static class Node {
        private int data;
        private Node next;

        public Node(int data, Node next) {
            this.data = data;
            this.next = next;
        }

        public int getData() {
            return data;
        }
    }

    public static void main(String[] args){
        LinkedListAlgo testInsert = new LinkedListAlgo();
//        Node head =  LinkedListAlgo.createNode(-1);

        Node node1 = LinkedListAlgo.createNode(1);
        Node node2 = LinkedListAlgo.createNode(2);
        Node node3 = LinkedListAlgo.createNode(3);
        Node node4 = LinkedListAlgo.createNode(4);

//        head.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

        LinkedListAlgo.printAll(node1);

        Node node5 = LinkedListAlgo.createNode(5);
        Node node6 = LinkedListAlgo.createNode(6);
        Node node7 = LinkedListAlgo.createNode(7);
        node5.next = node6;
        node6.next = node7;
        LinkedListAlgo.printAll(node5);

//        Node merged = LinkedListAlgo.mergeSortedLists(node1,node5);
//        Node merged = LinkedListAlgo.Merge(node1,node5);
//        LinkedListAlgo.printAll(merged);

//        Node middleNode = LinkedListAlgo.findMiddleNode(node1);
//        System.out.println("middleNode from:");
//        LinkedListAlgo.printAll(middleNode);

        Node reverseHead = LinkedListAlgo.reverse(node1);
//        Node reverseHead = LinkedListAlgo.reverse(node1);
//        Node reverseHead = LinkedListAlgo.ReverseList(node1);
        LinkedListAlgo.printAll(reverseHead);

    }

}
