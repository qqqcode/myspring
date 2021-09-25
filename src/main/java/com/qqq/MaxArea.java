package com.qqq;


import java.util.*;

/*
 ** 给你一个链表数组，每个链表都已经按升序排列。请你将所有链表合并到一个升序链表中，返回合并后的链表。
 * lists = [[1,4,5],[1,3,4],[2,6]]
 * 输出：[1,1,2,3,4,4,5,6]
 */

public class MaxArea {

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }


    public List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        if (n%2 == 1) {
            return new ArrayList<>();
        }

        int leftSize,rightSize = n/2;
        return null;
    }

    void dfs(List<String> res,int deep,int leftSize,int rightSize,int size) {
        if (deep >= size) {
            return;
        }

    }

    public static void main(String[] args) {

        MaxArea a = new MaxArea();

    }
}
