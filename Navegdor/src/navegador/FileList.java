/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package navegador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Nathan
 */

public class FileList {

    private static class ListNode {
        VirtualNode data;
        ListNode next;
        ListNode(VirtualNode data) { this.data = data; }
    }

    private ListNode head;
    private int size;

    public void add(VirtualNode vn) {
        ListNode newNode = new ListNode(vn);
        if (head == null) { head = newNode; }
        else {
            ListNode cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = newNode;
        }
        size++;
    }

    public int size() { return size; }

    public void mergeSort(Comparator<VirtualNode> cmp) {
        head = mergeSort(head, cmp);
    }

    private ListNode mergeSort(ListNode h, Comparator<VirtualNode> cmp) {
        if (h == null || h.next == null) return h;

        ListNode mid    = getMiddle(h);
        ListNode second = mid.next;
        mid.next = null;

        ListNode left  = mergeSort(h,      cmp);
        ListNode right = mergeSort(second, cmp);
        return merge(left, right, cmp);
    }

    private ListNode merge(ListNode a, ListNode b, Comparator<VirtualNode> cmp) {
        if (a == null) return b;
        if (b == null) return a;

        if (cmp.compare(a.data, b.data) <= 0) {
            a.next = merge(a.next, b, cmp);
            return a;
        } else {
            b.next = merge(a, b.next, cmp);
            return b;
        }
    }

    private ListNode getMiddle(ListNode h) {
        if (h == null) return h;
        ListNode slow = h, fast = h.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    public void bubbleSort(Comparator<VirtualNode> cmp) {
        if (head == null) return;
        boolean swapped;
        do {
            swapped = false;
            ListNode cur = head;
            while (cur.next != null) {
                if (cmp.compare(cur.data, cur.next.data) > 0) {
                    VirtualNode tmp = cur.data;
                    cur.data       = cur.next.data;
                    cur.next.data  = tmp;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }

    public List<VirtualNode> toList() {
        List<VirtualNode> result = new ArrayList<>();
        ListNode cur = head;
        while (cur != null) { result.add(cur.data); cur = cur.next; }
        return result;
    }
}
