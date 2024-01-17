package com.diagnomind.simulation;

// Java program that explains the internal
// implementation of BlockingQueue

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Queue<E> {
	private BlockingQueue<E> queue = new LinkedBlockingQueue<>();

	private int limit = 10;

	public Queue(int limit) { this.limit = limit; }

	public synchronized void enqueue(E item)
		throws InterruptedException
	{
		while (this.queue.size() == this.limit) {
			wait();
		}
		if (this.queue.isEmpty()) {
			notifyAll();
		}
		this.queue.add(item);
	}

	public synchronized E dequeue()
		throws InterruptedException
	{
		while (this.queue.isEmpty()) {
			wait();
		}
		if (this.queue.size() == this.limit) {
			notifyAll();
		}

        return this.queue.remove();
	}

}
