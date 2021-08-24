package com.qqq;


import java.math.BigDecimal;
import java.util.*;

public class Test {

    public static ListNode reverList(ListNode listNode) {
        ListNode headListNode= new ListNode(listNode.n);
        listNode = listNode.next;
        while (listNode != null) {
            ListNode listNode1 = new ListNode(listNode.n);
            listNode1.next = headListNode;
            headListNode = listNode1;
            listNode = listNode.next;
        }
        return headListNode;
    }


    public static void main(String[] args){
        String.join(":", Arrays.asList("qqq","www","eee"));
    }
}
