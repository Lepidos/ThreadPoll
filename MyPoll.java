import java.util.Queue;
import java.lang.Thread;
import java.util.HashMap;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyPoll {

	private Lock pollLock;
	private Queue<Condition> pollQueue;
	private int nextTicket;
	private int nextTurn;
	private int maxThreads;
	private HashMap<Integer,Thread> threadPollMap;

	private MyPoll() {
		this.pollLock = new ReentrantLock();
		this.pollQueue = new LinkedList<>();
		this.nextTicket = nextTurn = 0;
		this.maxThreads = 8;		//LIMITE DA POLL
		this.threadPollMap = new HashMap<>();

	}


	public Thread getThread(int ticket) {
		return this.threadPollMap.get(ticket);
	}

	public void registerThread(int ticket, Thread t) {
		this.threadPollMap.put(ticket, t);
	}

	public int newRequest() throws InterruptedException {
		pollLock.unlock();
		var ticket = nextTicket++;
		if (ticket>nextTurn) {
			var c = pollLock.newCondition();
			pollQueue.add(c);
			do
				c.await();
			while (ticket>nextTurn);
		}
// MAYBE ADD TO MAP HERE, BUT THREAD WASN'T DECLARED, BY PASSING IT TO THE FUNCTION
		pollLock.unlock();
		return ticket;
	}

	public void finishRequest(int ticket) throws InterruptedException {			//TALVEZ THREAD.JOIN() SEJA AQUI
		pollLock.unlock();
		this.threadPollMap.remove(ticket);  		//TALVEZ ISTO SEJA FORA DAQUI
		nextTurn++;
		var c = pollQueue.remove();
		if (c != null)
			c.signal();
		pollLock.unlock();
		this.getThread(ticket).join();
	}


	public static void main(String[] args) throws InterruptedException {		// FOR TEST PURPOSES
		MyPoll poll = new MyPoll();
		for (int i = 0; i < 20; i++) {
			final int ticket;
			try {
				ticket = poll.newRequest();				// ASK FOR A TICKET
			} catch (InterruptedException e) {
				continue;
			}
			poll.registerThread(ticket ,new Thread(new Runnable() {	// INITIALIZE THREAD AND ADD IT TO MAP
				@Override
				public void run() {
					try {
						System.out.println("Thread "  + String.valueOf(ticket) + " has started...");
						poll.finishRequest(ticket);			// DAR LUGAR AO PROXIMO NA FILA
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();

					}
				}
			}));
			poll.getThread(ticket).start();

		}
//		I DON'T KNOW WHERE TO PUT JOIN()
        }
}


