package ttt;

/* Author Matthew Vorsteg
 * this class provides a data structure to hold the profiles
 * slightly modified from a LinkedList Class I created in my freshman year of college
 * for a Java project then
 */

import java.util.Iterator;

public class BasicLinkedList<T> implements Iterable<T>{
	
	//inner class for a node
	protected class Node{
		protected Node next;
		protected Node prev;//points to next node in linkedlist
		protected T data; //contents of node
		//copy constructor
		protected Node(T data) {
			this.data = data;
			next = null;
			prev = null;
		}
		
		public String toString() {
			return data.toString();
		}
	}
	
	//instance variables
	protected Node globalHead;
	protected Node head;
	protected Node tail;
	protected int size;
	
	//default constructor
	public BasicLinkedList() {
		size = 0;
	}
	
	//adds a node associated with the parameter data to the end of the list
	public BasicLinkedList<T> add(T data){
		Node newNode = new Node(data);
		if(size == 0) {
			globalHead = newNode;
			head = newNode;
			tail = newNode;
		}else {
			head.prev = newNode;
			tail.next = newNode;
			newNode.prev = tail;
			newNode.next = head;
			tail = newNode;
		}
		size ++;
		return this;
	}
	
	//adds to front
	public BasicLinkedList<T> addToFront(T data){
		Node newNode = new Node(data);
		if(size == 0) {
			globalHead = newNode;
			head = newNode;
			tail = newNode;
		}else {
			newNode.next = globalHead;
			newNode.prev = tail;
			tail.next = newNode;
			globalHead.prev = newNode;
			globalHead = newNode;
		}
		size ++;
		return this;
	}
	
	//removes all instances of the target data from the list
	public BasicLinkedList<T> remove(){
		if (globalHead == head)
			globalHead = head.next;
		
		if (size >= 2) {
			head.prev.next = head.next;
			head.next.prev = head.prev;
			
		}else if (size == 1) {
			head = null;
			
		}
		Profile.numProfiles--;
		size--;
	
		
		return this;
	}
	
	//removes all instances of the target data from the list
		public BasicLinkedList<T> remove(String s){
			int num = 0;
			Node curr = head;
			
			while (num++ < size) {
				//System.out.println(curr.toString()+" "+s);
				if (curr.toString().compareTo(s)== 0) {
					if (globalHead == curr)
						globalHead = curr.next;
					
					if (size >= 2) {
						curr.prev.next = curr.next;
						curr.next.prev = curr.prev;
						
					}else if (size == 1) {
						curr = null;
						
					}
					//System.out.println("XXX");
					size--;
					Profile.numProfiles--;
				}
				curr = curr.next;
			}
			
			return this;
		}
	
	//required iterator method
	public java.util.Iterator<T> iterator(){
		return new Iterator<T>(){
			int num = 0;
			Node current = head;
			public boolean hasNext() {
				return (current != null && num++ < size);
			}
			public T next() {
				Node temp = current;
				current = current.next;
				return temp.data;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	//getter methods
	public T getFirst() {
		if(size == 0) {
			return null;
		}
		return head.data;
	}
	
	public T getLast() {
		if (size == 0) {
			return null;
		}
		return tail.data;
	}
	
	public int getSize() {
		return size;
	}
	
}

