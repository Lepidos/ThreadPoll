
private Class MyPoll {

	private Lock pollLock;
	private Queue<Condition> pollQueue;
	private int nextTicket;
	private int nextTurn;
	private int maxThreads;
	private HashMap<int,Thread> threadPollMap;

	private MyPoll() {
		this.pollLock = new ReentrantLock();
		this.pollQueue = new LinkedList<>();
		this.nextTicket = nextTurn = 0;
		this.maxThreads = 1;
		this.threadPollMap = new HashMap<>();

	}
	public Thread getThreadNumber(int number)
		return this.threadPollMap.get(number);

	public int newRequest(String name) throws InterruptedException {
		pollLock.unlock();
		var ticket = nextTicket++;
		if (ticket>nextTurn) {
			var c = pollLock.newCondition();
			pollQueue.add(c);
			do
				c.await();
			while (ticket>nextTurn);
		}
		threadPollMap.put(ticket, NULL);
		pollLock.unlock();
		return ticket;
	}

	public void finishRequest(int threadNumber) {	//TALVEZ THREAD.JOIN() SEJA AQUI
		pollLock.unlock();
		threadPollMapp.remove(threadNumber);
		nextTurn++;
		var c = pollQueue.remove();
		if (c != null)
			c.signal();
		pollLock.unlock();
	}


	public static void main(String[] args) throws InterruptedException {
		MyPoll threadPoll = new MyPoll();
		int numThreads = 0;
		for (int i = 0; i < 20; i++) {
			numThreads = threadPoll.newRequest();		// ASK FOR A TICKET
			this.threadPoolMap.put(numThreads,new Thread(new Runnable() {	// RUN JOB
				@Override
				public void run() {
					try {
						System.out.println("Thread " + (Thread.currentThread().getId() + 1) + " has started...");
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					threadPoll.finishRequest();		// GIVE PLACE TO NEXT IN QUEUE
				}
			}));
        	    t.start();
		}
		try {
			t.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
        }
    }
}
}
